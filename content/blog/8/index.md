---
title: 객체지향 생각해보기 - 밴픽 (3)
date: "2019-08-16"
---

[객체지향 생각해보기 - 밴픽 (1)](../1)
[객체지향 생각해보기 - 밴픽 (2)](../3)

## Continued

* TODO list
  * 게임이 성립되려면 챔피언 3명으로는 부족하다. 챔피언 갯수를 늘려야한다.
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

기왕 메소드를 분리시킨김에 아예 클래스를 나눠버렸다.

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

