public class FinalTest {
    
    public static void main(String[] args) {
        int totalRows1 = 0;
        int totalRows2 = 0;
        for (int i = 0; i < 100; i++) {
            State s = new State();
//          new TFrame(s);
          PlayerSkeleton p = new PlayerSkeleton(PlayerSkeleton.Constants.WEIGHTS);
          while (!s.hasLost()) {
              s.makeMove(p.pickMoveNormal(s, s.legalMoves()));
//              s.draw();
//              s.drawNext(0, 0);
//              try {
//                  Thread.sleep(300);
//              } catch (InterruptedException e) {
//                  e.printStackTrace();
//              }
          }
          System.out.println("A" + i + ": " + s.getRowsCleared());
          totalRows1 += s.getRowsCleared();
          
            s = new State();
//            new TFrame(s);
            p = new PlayerSkeleton(PlayerSkeleton.Constants.WEIGHTS);
            while (!s.hasLost()) {
                s.makeMove(p.pickMove(s, s.legalMoves()));
//                s.draw();
//                s.drawNext(0, 0);
//                try {
//                    Thread.sleep(300);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            System.out.println("B" + i + ": " + s.getRowsCleared());
            totalRows2 += s.getRowsCleared();
        }
        System.out.println("Without metabooster:\t" + totalRows1);
        System.out.println("With metabooster:\t" + totalRows2);
    }
}
