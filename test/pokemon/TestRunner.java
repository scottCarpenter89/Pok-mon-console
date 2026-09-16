package pokemon;

import pokemon.data.DataLoader;
import pokemon.data.GameData;

/**
 * Runs every test in the project.
 *
 * <pre>
 *   ./test.sh          (or)   java -cp out:test-out pokemon.TestRunner
 * </pre>
 *
 * <p>TEACHING NOTE (CS III - the software life cycle):
 * Run this before every commit. A test suite that takes one second to run and tells
 * you the truth is worth more than an afternoon of clicking through the game by
 * hand. The exit code is 0 when everything passes, so this also works in an
 * automated build.
 */
public final class TestRunner {

    public static void main(String[] args) {
        System.out.println("Pokemon Console RPG - test suite");

        GameData data = DataLoader.loadDefault();
        System.out.println("Loaded " + data.summary());

        TypeChartTest.run();
        DamageCalculatorTest.run();
        PokemonTest.run(data);
        BattleFlowTest.run(data);
        GameplayTest.run(data);
        DataIntegrityTest.run(data);

        System.exit(MiniTest.summary());
    }
}
