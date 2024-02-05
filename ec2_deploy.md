## 배포 방법

### MYSQL

- Mysql 설치

    - sudo dnf installhttps://dev.mysql.com/get/mysql80-community-release-el9-1.noarch.rpm
    - sudo dnf install mysql-community-server
    - sudo systemctl start mysqld

- 초기 패스워드 찾기

    - sudo grep 'temporary password' /var/log/mysqld.log
    - mysql -u root -p {log_password}

- 계정 권한 부여
    - ALTER USER ‘root@‘localhost’ identified by 'password';
    - create user 'dev_user'@'%' identified by 'password';
    - ALTER USER 'dev_user'@'%' IDENTIFIED WITH mysql_native_password BY 'password';
    - grant all privileges on \*.\* to 'dev_user'@'%';
    - flush privileges;

---

### JAVA

- jdk17 설치
    - sudo yum install java-17-amazon-corretto

---

### NODE

- node lts 설치
    - curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.34.0/install.sh | bash
    - ~/.nvm/nvm.sh
    - nvm install --lts
    - npm -v && node -v
    - npm install -g yarn && npm install pnpm -g

---

### 인증서

- certbot 설치
    - sudo yum install certbot
- 도메인 인증서 설치
    - sudo systemctl stop nginx
    - sudo certbot certonly --standalone -d {DOMAIN}
    - cd /etc/letsencrypt/live/{DOMAIN}
    - sudo chmod -R 755 *

- 자동갱신 등록
    - sudo yum install cronie
    - sudo crontab -e
    - 0 18 1 \* \* sudo certbot renew --pre-hook "sudo systemctl stop nginx" --post-hook "sudo systemctl restart nginx"

---

### nginx

- 설치

    - sudo yum update
    - sudo yum install nginx

- {domain}.conf 파일 등록
    - cd /etc/nginx/conf.d
    - sudo vi {DOMAIN}.conf

```
server {
    listen 80;
    server_name fc-on.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen       443 ssl;
    server_name  fc-on.com;

    ssl_certificate /etc/letsencrypt/live/$server_name/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/$server_name/privkey.pem;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;

    ssl_ciphers  HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers  on;

    location / {
        proxy_pass http://127.0.0.1:3001;
    }
}
```

- sudo nginx -t
- sudo systemctl start nginx

---

### GITHUB

- SSH_KEY 생성

    - ssh-keygen
    - cat ~/.ssh/id_rsa.pub
    - github 설정에 등록

- github clone
    - sudo yum install git
    - git clone git@github.com:khkil/find-me.git

---

### REACT

- pm2 설치

    - npm install -g pm2
    - pm2 ecosystem

    - 환경설정 파일 생성
      vi ecosystem.config.js

```
module.exports = {
  apps: [
    {
      name: "fc-on",
      script: "pnpm",
      args: "start",
      instances: 2,
      exec_mode: "cluster"
    }
  ]
};
```

- sudo vi deploy.sh

```
#!/bin/bash

# Git에서 최신 코드를 가져옴
git pull

# 종속성 설치
pnpm install

# Next.js 애플리케이션 빌드
pnpm build

# PM2로 애플리케이션 실행 또는 다시 시작
pm2 startOrReload ecosystem.config.js --env production
```

- chmod +x deploy.sh
- ./deploy.sh

---

### SPRING

- sudo vi deploy.sh

```deploy.sh
#!/bin/bash

# JAR 파일 경로
JAR_PATH="/home/ec2-user/fc-on/api/build/libs/fifa-0.0.1-SNAPSHOT.jar"
# 포트
PORT=8085
# 프로필
PROFILE="prod"

# 소스 코드 업데이트
echo "Pulling the latest changes from Git..."
git pull

# 빌드
echo "Building the project..."
./gradlew build

# 현재 실행 중인 프로세스 ID 확인
PID=$(lsof -ti :$PORT)

if [ -z "$PID" ]; then
  # 해당 포트를 사용하는 프로세스가 없으면 그냥 실행
  nohup java -jar $JAR_PATH --server.port=$PORT --spring.profiles.active=$PROFILE &
  echo "Application started on port $PORT with profile $PROFILE."
else
  # 해당 포트를 사용하는 프로세스가 있으면 종료 후 실행
  echo "Stopping application on port $PORT (PID: $PID)..."
  kill -9 $PID
  sleep 5
  echo "Starting application on port $PORT..."
  nohup java -jar $JAR_PATH --server.port=$PORT --spring.profiles.active=$PROFILE  &
  echo "Application restarted on port $PORT with profile $PROFILE."
fi
```

- sudo chmod +x deploy.sh
- ./deploy.sh

### Chrome

- Chrome 설치
    - wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm
    - sudo yum install ./google-chrome-stable_current_x86_64.rpm
- Chrome driver 설치
    - google-chrome --version (버전확인 ex: Google Chrome 120.0.6099.199 )
    - wget https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/120.0.6099.109/linux64/chromedriver-linux64.zip
    - mv chromedriver-linux64/chromedriver  ~/fc-on/api/ (드라이버 프로젝트 root path로 이동)

### Linux

- Timezone 한국시간대로 변경
    - sudo ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
    - date
    
    
