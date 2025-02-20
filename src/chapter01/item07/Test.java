package chapter01.item07;

import java.util.List;

class Member {
    private List<Integer> teamNumbers;
    public int age;

    public Member(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int ageChange(int plusAge) {
        return this.age += plusAge;
    }

    public List<Integer> getTeamNumbers() {
        return teamNumbers;
    }

    public void addTeamNumber(int teamNumber) {
        this.teamNumbers.add(teamNumber);
    }
}


public class Test {
    public static void main(String[] args) {
        Member member = new Member(20);

        // 값을 꺼내와서 직접 나이를 계산하여 원하는 나이 출력
        int age = member.getAge();
        int newAge1 = age++;

        // 나이 계산을 기존의 객체에게 시키기
        int newAge2 = member.ageChange(1);

        Member newMember1 = new Member(newAge1);
        Member newMember2 = new Member(newAge2);

        List<Integer> teamNumbers = member.getTeamNumbers();

        member = null;
        teamNumbers = null;

        List<Integer> teamNumbers1 = member.getTeamNumbers();

        teamNumbers1.add(3);

        member = null;

        // member.setAge(age);

        // 캡슐화... -> 정보 은닉,... 개발자는 기능을 사용만하고 직접적으로 뭘 안해도 값을 이용할수 이도록
        // 데이터를 숨기고 행동을 명확하게 정해서 접근하도록 한다... 정보은닉이잖아???
        // 캡슐화.... 왜냐면 값을 가져와서 우리가 요리하는것 ( 캡슐화가 깨진다 )
        // 불변 객체 -> 새로운 값으로 새로운 객체
        // 관점이 다르다... 앞서 내가 말했던
    }

    public static boolean isOlder(Member m1, Member m2) {
        return m1.age < m2.age;
    }
}
