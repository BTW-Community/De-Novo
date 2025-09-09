package btw.community.denovo.entity.mob.possession;

public class PossessionSource {

    public static class Sifting
            extends btw.entity.mob.possession.PossessionSource<Void> {
        public Sifting() {
            super(PossessionType.SIFTING);
        }
    }
}
