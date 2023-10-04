package com.simulation.fifa.api.batch.service;

import com.simulation.fifa.api.player.dto.AbilityDto;
import com.simulation.fifa.api.player.dto.SpIdDto;
import com.simulation.fifa.api.player.service.PlayerService;
import com.simulation.fifa.util.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BatchService {
    @Autowired
    PlayerService playerService;

    public void createPlayers() {
        List<SpIdDto> spIdList = playerService.getPlayerSpidList().subList(0, 2);
        for(SpIdDto spidDto : spIdList) {
            Long spId = spidDto.getId();
            try {
                Document document = Jsoup.connect("https://fconline.nexon.com/DataCenter/PlayerInfo?spid=" + spId).get();
                //Element positionEl = document.getElementsByClass("position_tabs").get(0);
                Elements abilities = document.getElementsByClass("content_bottom").get(0).getElementsByClass("ab");
                AbilityDto.Detail abilityInfo = new AbilityDto.Detail(spidDto.getId(), spidDto.getName());

                for(Element ability : abilities){
                    String name = ability.getElementsByClass("txt").get(0).html();
                    String value = RegexUtil.extractNumbers(ability.getElementsByClass("value").get(0).html());

                    abilityInfo.setValueFromText(name, Integer.parseInt(value));

                }
                System.out.println(abilityInfo.toString());
            }catch (IOException e) {
                System.out.println(e);
            }

        }
        System.out.println("test");
    }
}
