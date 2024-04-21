package com.simulation.fifa;

import org.springframework.boot.SpringApplication;

public class Solution {
    public int solution1(int[] income, int[] outlay, int cash) {
        long low = 0; // 최소값 설정 (0원)
        long high = 100000000L; // 최대값 설정 (1억원)
        int answer = 0;

        while (low <= high) {
            long mid = (low + high) / 2;

            long currentCash = cash;
            boolean canLoan = true;

            for (int i = 0; i < income.length; i++) {
                currentCash += income[i]; // 수입 추가
                currentCash -= (outlay[i] + mid); // 지출과 대출 금액 제거

                if (currentCash < 1) {
                    canLoan = false;
                    break;
                }
            }

            if (canLoan) {
                answer = (int) mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return answer;
    }

    public int solution2(int[] dials, int[] password) {
        int[] current = dials.clone(); // 현재 다이얼 상태를 복사하여 사용

        int totalMoves = 0;

        for (int i = 0; i < dials.length; i++) {
            int target = password[i];
            int currentDial = current[i];

            if (currentDial == target) {
                continue; // 현재 다이얼이 이미 목표값과 같으면 다음으로 넘어감
            }

            // 오른쪽으로 갈 때와 왼쪽으로 갈 때 비교하여 최소 횟수를 계산
            int clockwiseMoves = (target - currentDial + 10) % 10;
            int counterclockwiseMoves = (currentDial - target + 10) % 10;

            totalMoves += Math.min(clockwiseMoves, counterclockwiseMoves);

            // 다이얼 상태 업데이트: 현재 다이얼을 목표값에 맞춰 회전
            current[i] = target;

            System.out.println(current);

            // 오른쪽 다이얼들도 함께 돌리기
            for (int j = i + 1; j < dials.length; j++) {
                current[j] = (current[j] + clockwiseMoves) % 10;
            }
        }

        return totalMoves;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();

        int[] dials1 = {1, 5, 9, 3, 7};
        int[] password1 = {2, 5, 9, 3, 7};
        solution.solution2(dials1, password1); // 2

        int[] dials2 = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] password2 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        //   solution.solution2(dials2, password2); // 9
    }
}
