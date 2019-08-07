---
title: 객체지향 생각해보기 - 밴픽 (2)
date: "2019-08-06"
---

[객체지향 생각해보기 - 밴픽 (1)](../1)

## 실제 코드 구현

어느 정도 기초적인 구상이 완료되었으니(솔직히는 설계가 부족해보이지만 이 이상으로 어떻게 해야될지 모르겠어서..) 기초적인 기능을 수행하는 구현코드를 작성하려 한다. 

우선 챔피언을 객체를 생성하고 (Champion), 챔피언 풀에 등록하고 (ChampionPool), 유저가 풀에 있는 챔피언을 선택 (User)하는 동작을 구현할 것이다.

### Champion / ChampionPool

```java
public class Champion {
    private String name;
    private boolean available;

    public Champion(String name) {
        this.name = name;
        this.available = true;
    }

    public String getName() {
        return name;
    }

    public boolean getAvailable() {
        return available;
    }
}
```

Champion 클래스는 현재 계획으로는 워낙 간단한 클래스라 수정사항이 없다. 이름과 선택 가능 여부를 설정하는 생성자와 getter 정도만 구현한 상태.

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChampionPool {
    private List<Champion> champions;

    public ChampionPool() {
        champions = new ArrayList<>();
    }

    public void registerChampions(Champion... newChampions) {
        for(Champion champion : newChampions) 
            champions.add(champion);
    }

    public Champion getChampion(int index) {
        return champions.get(index);
    }

    public List<Champion> getChampions() {
        return champions;
    }
    
    public void showChampions() {
        StringBuilder sb = new StringBuilder();

        for(Champion champion : champions) {
            String championName = champion.getAvailable() ? champion.getName() : "";
            sb.append( String.format("%-10s", championName) );
        }

        System.out.println(sb.toString());
    }

    public Champion randomSelect() {
        Champion candidate = null;
        while(true) {
            candidate = champions.get( (int)(Math.random() * champions.size()) );
            if(candidate.getAvailable()) {
                return candidate;
            }
        }
    }
}
```

ChampionPool 클래스에는 새로운 챔피언을 등록하는 registerChampions() 메소드, 챔피언 목록을 출력해주는 showChampions() 메소드, 등록된 챔피언 중에서 랜덤으로 하나를 선택해서 리턴해주는 randomSelect() 메소드를 구현했다.  
Champion 객체의 available 필드로 이미 선택됐거나 밴당한 챔피언은 목록에 출력되거나, 랜덤 선택 되지 않도록 했다.

```java
public interface Summoner { 
    public Champion selectChampion(ChampionPool champions); 

    public String getSummonerId();
    public Champion getPick();
    public void setPick(Champion champion);
}
```

지난번 마지막에 설계를 바꾸면서 생긴 Summoner 인터페이스이다. 소환사는 챔피언을 선택(*밴, 픽 모두 포함*)하는 기능을 포함하고 있어야하며, 픽한  챔피언 정보를 갖고 있어야 한다. Summoner 인터페이스를 구현하는 User 와 Ai를 나눠서 만들었다.

```java
import java.util.Scanner;

public class User implements Summoner {
    private String summonerId;
    private Champion pick;
    private boolean turn;

    public User(String summonerId) {
        this.summonerId = summonerId;
    }

    @Override
    public Champion selectChampion(ChampionPool champions) {
        System.out.println("선택 가능 챔피언 목록");
        champions.showChampions();
        System.out.println("랜덤 선택 : -1");
        Scanner sc = new Scanner(System.in);

        Champion selectedChampion = getSelectChampion(champions, sc.nextLine());
        selectedChampion.setDisable();
        return selectedChampion;
    }

    @Override
    public void setPick(Champion champion) {
        this.pick = champion;
    }

    @Override
    public Champion getPick() {
        return pick;
    }

    @Override
    public String getSummonerId() {
        return summonerId;
    }

    private Champion getSelectChampion(ChampionPool champions, String in) {
        int sel = Integer.parseInt(in);

        switch(sel) {
            case -1 :
                return champions.randomSelect();
            default :
                return champions.getChampion(sel);
        }
    }
}
```

```java
public class Ai implements Summoner {
    private String summonerId;
    private Champion pick;
    private boolean turn;

    public Ai(String summonerId) {
        this.summonerId = summonerId;
    }

    @Override
    public Champion selectChampion(ChampionPool champions) {
        Champion selected = champions.randomSelect();
        selected.setDisable();
        return selected;
    }

    @Override
    public void setPick(Champion champion) {
        this.pick = champion;
    }

    @Override
    public Champion getPick() {
        return pick;
    }

    @Override
    public String getSummonerId() {
        return summonerId;
    }
}
```

User와 Ai의 유일한 차이는 *-챔피언을 스스로 고를 수 있는가?-* 이다. Ai는 ChampionPool에서 골라주는 랜덤 챔피언만을 선택할 수 있는 반면, User는 랜덤은 물론 스스로 선택할 수도 있어야한다. 때문에 selectChampion() 메소드가 다르게 구현되어야 한다.  
Ai의 selectChampion()에서는 바로 랜덤 선택된 챔피언을 리턴하지만, User에서는 선택 가능한 챔피언 목록을 출력하고 직접 입력 받는 동작 후에 선택된 챔피언을 리턴한다.

```java
import java.util.ArrayList;
import java.util.List;

public class Room {
    private LolMap map;
    private List<Summoner> summoners;
    private ChampionPool championPool;
    
    private List<Champion> banList;
    private List<Champion> pickList;

    public Room(LolMap map, List<Summoner> summoners) {
        this.map = map;
        this.summoners = summoners;
        initChampionPool();

        banList = new ArrayList<>();
        pickList = new ArrayList<>();
    }

    public void progressBan() {
        for(Summoner summoner : summoners) {
            Champion selected = summoner.selectChampion(championPool);
            banList.add(selected);
        }
    }

    public void progressPick() {
        for(Summoner summoner : summoners) {
            System.out.println("- " + summoner.getSummonerId() + " 소환사님 선택 차례 -");
            Champion selected = summoner.selectChampion(championPool);
            summoner.setPick(selected);
            pickList.add(selected);
            System.out.println(summoner.getSummonerId() + "님의 픽 : " + selected.getName());
        }
    }

    private void initChampionPool() {
        championPool = new ChampionPool();

        Champion garen = new Champion("가렌"); 
        Champion ashe = new Champion("애쉬");
        Champion ahri = new Champion("아리");

        championPool.registerChampions(garen, ashe, ahri);
    }
}
```

Room 객체는 밴픽을 진행하는 주체가 된다. 맵과 게임에 참여할 소환사 목록을 전달받으면서 생성이 되면, 새로운 ChampionPool 객체를 하나 생성하고, 밴픽 리스트를 생성하면서 밴픽을 진행할 준비를 한다. 추후에 밴픽이 진행되면서 선택된 챔피언은 각각의 리스트에 추가된다. 현재 단계에서는 progressPick만 구체적으로 구현했지만, progressBan의 동작도 크게 다르지는 않을 것이기 때문에 간단하게 구현은 해놨다.

Room 객체 입장에서는 방에 들어온 *<b>소환사가 실제 유저인지, Ai인지 신경 쓸 필요가 없다.</b>* 그냥 밴픽을 진행하면서 순서가 된 소환사에게 챔피언을 선택하라고 요청하기만 할 뿐이다. 때문에 User, Ai로 소환사를 각각 관리하는게 아니라 <U>Summoner 인터페이스</U>를 통해 관리한다.

initChampionPool()은 방이 새로 생성되면서 새로운 챔피언 풀을 생성하는 작업을 수행한다.  
이 메소드가 Room 클래스에 있어야하는지, ChampionPool 클래스에 있어야하는지는 고민이다. 챔피언 풀과 관련된 기능들은 ChampionPool 클래스가 갖고 있긴 하지만 *맵에 따라 선택 가능한 챔피언이 다르다면?* 똑같은 init 메소드를 통해 챔피언 풀을 초기화하면 안될 것이다.  
(쓰면서 드는 생각인데 Room과 ChampionPool 클래스 각각에 init 관련 메소드를 만들어서, Room은 init을 위한 정보 생성, ChampionPool은 실제 객체 생성을 수행하면 될 것 같다.)

```java
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Summoner u1 = new User("에블바디언더스텐");
        Summoner u2 = new Ai("ManySolutions");
        Summoner u3 = new User("바데야");
        List<Summoner> entry = new ArrayList<>();
        entry.add(u1);
        entry.add(u2);
        entry.add(u3);

        Room room = new Room(null, entry);
        room.progressPick();
    }
}
```

유저 두명과 AI 하나를 생성해서 밴픽을 진행해봤다. 방을 하나 새로 파서 생성된 소환사 목록을 전달한다. (Map 관련 구현은 아직 안됐기 때문에 null을 던져준다.)  
일단 챔피언 선택 기능과 랜던 선택이 잘 동작하는지를 확인하는 것이 목표이기 때문에 progressPick() 만으로도 충분할 것 같다.

```
실행결과
- 에블바디언더스텐 소환사님 선택 차례 -
선택 가능 챔피언 목록
가렌        애쉬        아리        
랜덤 선택 : -1
1
에블바디언더스텐님의 픽 : 애쉬
- ManySolutions 소환사님 선택 차례 -
ManySolutions님의 픽 : 아리
- 바데야 소환사님 선택 차례 -
선택 가능 챔피언 목록
가렌                            
랜덤 선택 : -1
-1
바데야님의 픽 : 가렌
```

*(1)첫번째 유저의 직접 선택*, *(2)Ai의 랜덤 선택*, *(3)두번째 유저의 랜덤 선택* 까지 원하는대로 동작하는걸 확인.