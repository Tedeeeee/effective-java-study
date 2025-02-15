package chapter01.item06;

interface OldCharger {
    void charge110V();
}

class Old110VCharger implements OldCharger {
    public void charge110V() {
        System.out.println("110V 충전 중,....");
    }
}

interface NewCharger {
    void charge220V();
}

class ChargerAdapter implements NewCharger {
    private final OldCharger oldCharger;

    public ChargerAdapter(OldCharger oldCharger) {
        this.oldCharger = oldCharger;
    }

    @Override
    public void charge220V() {
        System.out.println("220V로 변환되어 충전 중....");
        oldCharger.charge110V();
    }
}

public class AdapterExam {
    public static void main(String[] args) {
        Old110VCharger old110VCharger = new Old110VCharger();

        ChargerAdapter chargerAdapter = new ChargerAdapter(old110VCharger);

        chargerAdapter.charge220V();
    }
}
