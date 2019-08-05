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

```java
public class ChampionPool {
    private List<Champion> champions;

    public ChampionPool() {
        champions = new ArrayList<>();
    }

    public void registerChampions(Champion... newChampions) {
        for(Champion champion : newChampions)
            champions.add(champion);
    }
    
    public void showChampions() {
        StringBuilder sb = new StringBuilder();

        for(Champion champion : champions) {
            sb.append( String.format("%-10s", champion.getName()) );
        }

        System.out.println(sb.toString());
    }

    public Champion randomSelect() {
        return champions.get( (int) (Math.random() * champions.size()) );
    }
}
```

Champion 클래스는 현재 계획으로는 워낙 간단한 클래스라 수정사항이 없다.  
ChampionPool 클래스에는 새로운 챔피언을 등록하는 registerChampions() 메소드, 챔피언 목록을 출력해주는 showChampions() 메소드, 등록된 챔피언 중에서 랜덤으로 하나를 선택해서 리턴해주는 randomSelect() 메소드를 구현했다. 이 중 showChampions, randomSelect 메소드는 추후에 <U>Ban 당한 챔피언은 목록에서 제외하는 기능이 추가돼야 한다.</U>