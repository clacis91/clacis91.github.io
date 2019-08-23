---
title: 객체지향 생각해보기 - 밴픽 (3)
date: "2019-08-16"
---

[객체지향 생각해보기 - 밴픽 (1)](../1)
[객체지향 생각해보기 - 밴픽 (2)](../3)

## Continued

* TODO list
  * 게임이 성립되려면 챔피언 3명으로는 부족하다. 챔피언 갯수를 늘려서 밴픽을 진행해보자.
  * 챔피언은 100종류가 넘는다. 이걸 일일히 코드상에서 추가해줄 수는 없다.  
  -> DB(일단은 파일형태)에 챔피언 정보를 추가하는 것 만으로도 챔피언 풀에 추가돼야한다.
  * 지금은 챔피언 정보에 이름만 들어가지만 언제 갑자기 추가적인 정보가 필요해질지 모른다.   
  -> 챔피언 생성 과정에서 챔피언 객체와 관련된 의존성을 최대한 제거해야 한다.

### ChampionPoolGenerator

```java
// Room Class
private void initChampionPool() {
    championPool = new ChampionPool();

    Champion garen = new Champion("가렌"); 
    Champion ashe = new Champion("애쉬");
    Champion ahri = new Champion("아리");

    championPool.registerChampions(garen, ashe, ahri);
}
```

기존 단계에서 챔피언 풀을 생성하는 동작을 Room 객체에서 수행하고 있었다. 이 동작은 엄밀히 보면 <b>(1)챔피언 풀을 생성</b>하고 <b>(2)생성된 챔피언 풀을 Room에 등록</b>하는 두가지 일을 한번에 수행하고 있기 때문에 적절치 않다. Room 객체에서는 만들어진 챔피언 풀을 가져다 쓰기만 하는게 맞다. 때문에 챔피언 풀을 생성하는 클래스를 새로 만들기로 했다.

챔피언 풀을 생성하는 클래스는 이전처럼 챔피언 정보를 코드에 일일히 입력해서 등록하는게 아니라 DB에서 읽어와서 등록하는 방식으로 수정했다. (현재 단계에서는 간단한 text file로 DB를 흉내냄, 한줄 당 챔피언 이름 하나가 적혀있음)

```java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

public class ChampionPoolGenerator {
    private static final String CHAMPION_DB_FILE = "champion.db";
    private Stream dbStream;
    private ChampionPool championPool;

    public ChampionPoolGenerator() {
        championPool = new ChampionPool();
    }

    public ChampionPool generate() {
        dbStream = DBReader();
        if(dbStream == null) {
            System.out.println("Champion DB error!");
            return null;
        }

        Iterator iter = dbStream.iterator();
        while(iter.hasNext()) {
            Champion champion = parseChampion(iter.next());
            championPool.registerChampions(champion);
        }
        return championPool;
    }

    private Champion parseChampion(Object iter) {
        String line = String.valueOf(iter);
        return new Champion(line);
    }

    private Stream DBReader() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(CHAMPION_DB_FILE)));
            return br.lines();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        return null;
    }
}
```

```java
// Room.java
...
public Room(LolMap map, List<Summoner> summoners) {
    ChampionPoolGenerator championPoolGenerator = new ChampionPoolGenerator();
    this.map = map;
    this.summoners = summoners;
    //initChampionPool();
    //=>
    championPool = championPoolGenerator.generate();

    banList = new ArrayList<>();
    pickList = new ArrayList<>();
}
...
```

기존 Room 객체 안에서 init 메소드로 챔피언 풀을 초기화하던 방식에서 ChampionPoolGenerator로 챔피언 풀 인스턴스를 생성하여 관리하도록 변경하였다.

ChampionPoolGenerator 구현시 신경쓴 부분이 있는데, 한 클래스 안에서도 DB로부터 챔피언 정보를 불러오는 메소드와 챔피언 풀 객체를 생성하는 메소드를 분리해야한다는 점이었다.

현재 DB는 허접한 File IO 방식으로 구현되어 있지만, 언제 갑자기 열정이 샘솟아서 DBMS를 활용해서 DB를 구축하게 될지 모르는 일이다. 하지만 챔피언 풀을 생성하는 메소드는 DB가 file이건, MySQL이건, MongoDB건 상관하지 않고 원하는 형태로 데이터가 들어오기만 하면 챔피언 풀 객체를 생성할 수 있어야한다. 

기왕 메소드 기능을 분리시킨김에 아예 클래스를 나눠버렸다.

### ChampionDBManager

```java
import java.util.stream.Stream;

public interface ChampionDBManager {
    public Champion parseChampion(Object iter);
    public Stream DBReader();
}
```

ChampionDBManager 인터페이스를 선언했다. 앞으로 챔피언 DB와 관련된 클래스는 이 인터페이스를 구현해야한다.

|Metohd Name|Description|
|:--:|:----------|
|DBReader|DB로부터 데이터를 읽어오는 메소드|
|parseChampion|DB로부터 읽어온 데이터를 Champion 객체로 만들어서 반환해주는 메소드|

```java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Stream;

public class ChampionFileDBManager implements ChampionDBManager {
    private static final String CHAMPION_DB_FILE = "champion.db";

    @Override
    public Champion parseChampion(Object iter) {
        String line = String.valueOf(iter);
        String[] championData = line.split(",");

        int index = Integer.parseInt(championData[0]);
        String name = championData[1];

        return new Champion(name, index);
    }

    @Override
    public Stream DBReader() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(CHAMPION_DB_FILE)));
            return br.lines();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        return null;
    }
}
```

ChampionFileDBManager는 File DB를 채택한 경우 사용할 클래스를 구현체가 된다. 만약 다른 DB를 사용하게 된다면 ChampionMySqlDBManager 클래스 같은걸 ChampionDBManager를 인터페이스로 해서 구현하면 될 것이다.

```java
import java.util.Iterator;
import java.util.stream.Stream;

public class ChampionPoolGenerator {
    private ChampionDBManager championDBManager;
    private Stream dbStream;

    private ChampionPool championPool;

    public ChampionPoolGenerator() {
        championPool = new ChampionPool();
        championDBManager = new ChampionFileDBManager();
    }

    public ChampionPool generate() {
        dbStream = championDBManager.DBReader();
        if(dbStream == null) {
            System.out.println("Champion DB error!");
            return null;
        }

        Iterator iter = dbStream.iterator();
        while(iter.hasNext()) {
            Champion champion = championDBManager.parseChampion(iter.next());
            championPool.registerChampions(champion);
        }
        return championPool;
    }
}
```

챔피언 풀 생성을 위해 ChampionDBManager 인스턴스를 하나 생성해서 챔피언 정보를 불러온다. DB가 변경됐다면?  
*championDBManager = new ChampionFileDBManager();* 에서 *ChampionFileDBManager만* 바꿔주면 될 것 같다.

이렇게 분리하고 나니 ChampionPoolGenerator가 *singleton*이어도 문제 없을 것 같다는 생각이 들었다. 챔피언 풀 인스턴스를 찍어내는 기계는 딱 하나만 존재해도 문제 없다.  
단, 여러개의 방에서 동시에 챔피언 풀 생성을 요청하는 경우를 고려해야 한다. generate 내부에서 Iterator 루프가 도는 중에 다른 곳에서 generate를 호출하면 루프에 영향을 주는일이 없는지? synchronized로 해결되는지? 이런거...  
일단은 TODO로

```
#champion.db
1,가렌
2,갈리오
3,갱플랭크
...
18,라칸
19,람머스
20,럭스
```

일단 파일에는 (귀찮아서) 20개의 챔피언 정보만 가나다 순으로 등록해뒀다. 그리고 혹시모를 인덱싱을 위해 챔피언 번호도 추가. 파일 포맷이 변경되어도 parseChampion 메소드만 잘 수정하면 다른 곳은 건드릴 필요가 없다.

```
--실행화면--
- 에블바디언더스텐 소환사님 선택 차례 -
선택 가능 챔피언 목록
가렌        갈리오       갱플랭크      그라가스      그레이브스     나르        나미        나서스       노틸러스      녹턴        누누와 윌럼프   니달리       니코        다리우스      다이애나      드레이븐      라이즈       라칸        람머스       럭스        
랜덤 선택 : -1
```

파일에 추가한대로 챔피언 목록이 잘 출력된다. (선택도 잘 된다.)  
챔피언이 충분하니 이제 3대3으로 간단한 밴픽을 진행해봐도 될 것 같다.

```java
// Main.java
...

Summoner ai1 = new Ai("봇1");
Summoner ai2 = new Ai("봇2");
Summoner ai3 = new Ai("봇3");
entry.add(ai1);
entry.add(ai2);
entry.add(ai3);

Room room = new Room(null, entry);

room.progressBan();
room.progressPick();
room.printBanPickResult();

...
```

미리 만들어놓은 progressBan() 메소드를 progressPick() 전에 호출해서 밴-픽 순서로 진행을 하고, 결과를 출력해주는 메소드를 하나 만들었다. 

```
=== 밴픽 결과 ===
금지된 챔피언 목록[3. 갱플랭크, 5. 그레이브스, 8. 나서스, 14. 다리우스, 6. 나르, 13. 니코]
챔피언 선택 결과
에블바디언더스텐 님 : 그라가스
ManySolutions 님 : 노틸러스
바데야 님 : 녹턴
봇1 님 : 럭스
봇2 님 : 라이즈
봇3 님 : 나미
```

---

밴픽을 위한 챔피언 풀 준비 단계와, 선택 결과가 제대로 동작한다는 것은 확인했다.  
이제 야매로 구현돼있는 밴픽 부분을 다듬을 차례다.

일단 밴픽을 진행하려면 논리적으로 팀이 먼저 구성되어야 하고, 어떤 순서로 밴픽을 진행할지 결정하기 위한 룰이 필요하다.