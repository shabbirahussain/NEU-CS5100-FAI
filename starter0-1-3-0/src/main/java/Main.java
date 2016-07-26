import entrants.pacman.shabbir.MyPacMan;
import entrants.pacman.shabbir.SmartController;
import examples.commGhosts.POCommGhosts;
import pacman.Executor;
import examples.poPacMan.POPacMan;
import pacman.controllers.examples.RandomGhosts;

/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    public static void main(String[] args) {

        Executor executor = new Executor(false, true);

        //executor.runGameTimed(new POPacMan(), new POCommGhosts(50), true);
        executor.runGameTimed(new SmartController(), new RandomGhosts(), true);
    }
}
