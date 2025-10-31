package worksheet1;

import java.awt.*;
import java.util.function.DoubleUnaryOperator;

class PlottableFunction {
    final String name;
    final Color color;
    final DoubleUnaryOperator func;

    PlottableFunction(String name, Color color, DoubleUnaryOperator func) {
        this.name = name;
        this.color = color;
        this.func = func;
    }
}
