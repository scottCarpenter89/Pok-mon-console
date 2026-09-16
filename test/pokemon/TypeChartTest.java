package pokemon;

import pokemon.model.Type;

/** Tests for the type-effectiveness chart. */
public class TypeChartTest {

    public static void run() {
        MiniTest.section("Type chart");

        MiniTest.checkEquals("Fire is strong against Grass", 2.0,
                Type.effectiveness(Type.FIRE, Type.GRASS));
        MiniTest.checkEquals("Water is strong against Fire", 2.0,
                Type.effectiveness(Type.WATER, Type.FIRE));
        MiniTest.checkEquals("Grass is weak against Fire", 0.5,
                Type.effectiveness(Type.GRASS, Type.FIRE));
        MiniTest.checkEquals("Electric does nothing to Ground", 0.0,
                Type.effectiveness(Type.ELECTRIC, Type.GROUND));
        MiniTest.checkEquals("Normal does nothing to Ghost", 0.0,
                Type.effectiveness(Type.NORMAL, Type.GHOST));
        MiniTest.checkEquals("Ghost does nothing to Normal", 0.0,
                Type.effectiveness(Type.GHOST, Type.NORMAL));
        MiniTest.checkEquals("Dragon does nothing to Fairy", 0.0,
                Type.effectiveness(Type.DRAGON, Type.FAIRY));

        // Dual types multiply - the famous 4x weakness.
        MiniTest.checkEquals("Electric vs Water/Flying is 4x", 4.0,
                Type.effectiveness(Type.ELECTRIC, Type.WATER, Type.FLYING));
        MiniTest.checkEquals("Rock vs Fire/Flying is 4x", 4.0,
                Type.effectiveness(Type.ROCK, Type.FIRE, Type.FLYING));
        MiniTest.checkEquals("Fire vs Grass/Poison is 2x", 2.0,
                Type.effectiveness(Type.FIRE, Type.GRASS, Type.POISON));
        MiniTest.checkEquals("Grass vs Grass/Poison is 0.25x", 0.25,
                Type.effectiveness(Type.GRASS, Type.GRASS, Type.POISON));
        MiniTest.checkEquals("A null second type is ignored", 1.0,
                Type.effectiveness(Type.NORMAL, Type.NORMAL, null));

        // Structural checks: the chart must be complete and sane.
        boolean allDefined = true;
        for (Type attacking : Type.values()) {
            for (Type defending : Type.values()) {
                double value = Type.effectiveness(attacking, defending);
                if (value != 0.0 && value != 0.5 && value != 1.0 && value != 2.0) {
                    allDefined = false;
                }
            }
        }
        MiniTest.check("Every one of the " + (Type.values().length * Type.values().length)
                + " chart cells holds a legal multiplier", allDefined);

        MiniTest.checkThrows("An unknown type name is rejected",
                IllegalArgumentException.class, () -> Type.parse("sparkle"));
        MiniTest.checkEquals("Type names parse case-insensitively", Type.FIRE, Type.parse("fire"));
    }
}
