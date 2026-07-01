# Interpreter Pattern

> **Defines a grammatical representation for a language and an interpreter to interpret sentences in that language. Used to evaluate expressions or sentences in a language.**

## 📖 Concept

**Real-world analogy:** Google Translate — you write in English, the interpreter translates to Hindi. The interpreter knows the grammar rules of both languages.

## 🔍 When to Use

- Need to evaluate expressions or sentences in a language
- Grammar is relatively simple
- Performance is not critical
- Need to interpret a domain-specific language
- SQL parsing, regular expressions, rule engines

## ✅ Interview Checklist

- [ ] Abstract Expression declares interpret method
- [ ] Terminal Expression implements interpretation for literals
- [ ] Non-Terminal Expression implements interpretation for rules
- [ ] Context holds global information for interpretation
- [ ] Build Abstract Syntax Tree (AST) to interpret

## 🧪 Common Interview Question

**Problem:** Design a Boolean Expression Interpreter that can evaluate expressions like `true AND (false OR true)` and `x > 5 AND y < 10`.

## 💻 Java Implementation

### 1. Basic Boolean Expression Interpreter

```java
// Abstract Expression
interface BooleanExpression {
    boolean interpret(Context context);
}

// Context
class Context {
    private Map<String, Boolean> variables = new HashMap<>();

    public void assign(String variable, boolean value) {
        variables.put(variable, value);
    }

    public boolean lookup(String variable) {
        return variables.getOrDefault(variable, false);
    }
}

// Terminal Expressions
class VariableExpression implements BooleanExpression {
    private String variable;
    public VariableExpression(String variable) { this.variable = variable; }
    @Override
    public boolean interpret(Context context) {
        return context.lookup(variable);
    }
}

class ConstantExpression implements BooleanExpression {
    private boolean value;
    public ConstantExpression(boolean value) { this.value = value; }
    @Override
    public boolean interpret(Context context) {
        return value;
    }
}

// Non-Terminal Expressions
class AndExpression implements BooleanExpression {
    private BooleanExpression left, right;
    public AndExpression(BooleanExpression left, BooleanExpression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public boolean interpret(Context context) {
        return left.interpret(context) && right.interpret(context);
    }
}

class OrExpression implements BooleanExpression {
    private BooleanExpression left, right;
    public OrExpression(BooleanExpression left, BooleanExpression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public boolean interpret(Context context) {
        return left.interpret(context) || right.interpret(context);
    }
}

class NotExpression implements BooleanExpression {
    private BooleanExpression expression;
    public NotExpression(BooleanExpression expression) {
        this.expression = expression;
    }
    @Override
    public boolean interpret(Context context) {
        return !expression.interpret(context);
    }
}
```

### 2. Usage

```java
public class InterpreterDemo {
    public static void main(String[] args) {
        Context context = new Context();
        context.assign("x", true);
        context.assign("y", false);

        // x AND (y OR true)
        BooleanExpression expr = new AndExpression(
            new VariableExpression("x"),
            new OrExpression(
                new VariableExpression("y"),
                new ConstantExpression(true)
            )
        );

        System.out.println(expr.interpret(context)); // true
    }
}
```

### 3. Full Working Example: Mathematical Expression Interpreter

```java
import java.util.Stack;

// Abstract Expression
interface Expression {
    int evaluate();
}

// Terminal Expressions (numbers)
class NumberExpression implements Expression {
    private int number;
    public NumberExpression(int number) { this.number = number; }
    @Override public int evaluate() { return number; }
}

// Non-Terminal Expressions (operations)
class AddExpression implements Expression {
    private Expression left, right;
    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override public int evaluate() {
        return left.evaluate() + right.evaluate();
    }
}

class SubtractExpression implements Expression {
    private Expression left, right;
    public SubtractExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override public int evaluate() {
        return left.evaluate() - right.evaluate();
    }
}

class MultiplyExpression implements Expression {
    private Expression left, right;
    public MultiplyExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override public int evaluate() {
        return left.evaluate() * right.evaluate();
    }
}

class DivideExpression implements Expression {
    private Expression left, right;
    public DivideExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override public int evaluate() {
        return left.evaluate() / right.evaluate();
    }
}

// Expression Parser
class ExpressionParser {
    public static Expression parse(String expression) {
        Stack<Expression> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c)) {
                operands.push(new NumberExpression(Character.getNumericValue(c)));
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    applyOperator(operands, operators.pop());
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            applyOperator(operands, operators.pop());
        }

        return operands.pop();
    }

    private static int precedence(char op) {
        return switch (op) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }

    private static void applyOperator(Stack<Expression> operands, char op) {
        Expression right = operands.pop();
        Expression left = operands.pop();

        Expression result = switch (op) {
            case '+' -> new AddExpression(left, right);
            case '-' -> new SubtractExpression(left, right);
            case '*' -> new MultiplyExpression(left, right);
            case '/' -> new DivideExpression(left, right);
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };

        operands.push(result);
    }
}

// Usage
public class MathDemo {
    public static void main(String[] args) {
        // Parses: 3+5*2-8/4 = 3+10-2 = 11
        Expression expr = ExpressionParser.parse("3+5*2-8/4");
        System.out.println("Result: " + expr.evaluate()); // 11
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Complex grammar becomes hard to maintain | Use parser generators (ANTLR) instead |
| Performance overhead | Cache parsed AST, use compiled expressions |
| Too many expression classes | Use interpreter only for simple grammars |
| Error handling | Provide meaningful error messages |

## 🎯 Related Interview Questions

1. **Design an Expression Evaluator** — Parse and evaluate `(3+5)*2`
2. **Design a Search Query Parser** — `price > 100 AND category = "Electronics"`
3. **Regular Expression Engine** — How regex engines work internally

## 🆚 Interpreter vs Visitor

| Aspect | Interpreter | Visitor |
|--------|-------------|---------|
| Purpose | Evaluate language expressions | Perform operations on objects |
| Structure | Defines grammar + interpreter | Separates algorithm from object structure |
| AST | Builds and evaluates AST | Traverses existing object structure |
| Use | Domain-specific languages | Adding operations without changing classes |