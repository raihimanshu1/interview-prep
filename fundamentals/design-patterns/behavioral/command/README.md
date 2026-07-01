# Command Pattern

> **Encapsulates a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.**

## 📖 Concept

**Real-world analogy:** A waiter takes your order (command). The order can be queued, logged, and even cancelled if needed. The kitchen executes it.

## 🔍 When to Use

- Need to parameterize objects with operations (callbacks)
- Need to queue, schedule, or log operations
- Need undo/redo functionality
- Support transactional behavior (rollback)
- Want to decouple invoker from receiver

## ✅ Interview Checklist

- [ ] Command interface with execute() method
- [ ] Receiver performs actual work
- [ ] Concrete Command wraps Receiver and parameters
- [ ] Invoker holds and executes commands
- [ ] Support undo by storing inverse operations

## 🧪 Common Interview Question

**Problem:** Design a TV Remote with Undo/Redo. Commands: TurnOn, TurnOff, VolumeUp, VolumeDown. The remote should support undo.

## 💻 Java Implementation

### 1. Basic Command

```java
// Command Interface
interface Command {
    void execute();
    void undo();
}

// Receiver
class Television {
    private boolean isOn = false;
    private int volume = 10;

    public void turnOn() { isOn = true; System.out.println("TV turned ON"); }
    public void turnOff() { isOn = false; System.out.println("TV turned OFF"); }
    public void volumeUp() { volume++; System.out.println("Volume: " + volume); }
    public void volumeDown() { volume--; System.out.println("Volume: " + volume); }
}

// Concrete Commands
class TurnOnCommand implements Command {
    private Television tv;
    public TurnOnCommand(Television tv) { this.tv = tv; }
    @Override public void execute() { tv.turnOn(); }
    @Override public void undo() { tv.turnOff(); }
}

class VolumeUpCommand implements Command {
    private Television tv;
    public VolumeUpCommand(Television tv) { this.tv = tv; }
    @Override public void execute() { tv.volumeUp(); }
    @Override public void undo() { tv.volumeDown(); }
}

// Invoker
class RemoteControl {
    private Stack<Command> history = new Stack<>();
    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }
    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            command.undo();
        }
    }
}
```

### 2. Usage

```java
public class CommandDemo {
    public static void main(String[] args) {
        Television tv = new Television();
        RemoteControl remote = new RemoteControl();
        remote.executeCommand(new TurnOnCommand(tv));
        remote.executeCommand(new VolumeUpCommand(tv));
        remote.executeCommand(new VolumeUpCommand(tv));
        remote.undo(); // Volume down
        remote.undo(); // Volume down
    }
}
```

### 3. Full Working Example: Text Editor with Undo/Redo

```java
import java.util.Stack;

// Receiver
class TextEditor {
    private StringBuilder text = new StringBuilder();

    public void addText(String s) { text.append(s); }
    public void deleteLast(int n) { text.delete(text.length()-n, text.length()); }
    public String getText() { return text.toString(); }
}

// Command
interface TextCommand {
    void execute();
    void undo();
}

// Concrete Commands
class AddTextCommand implements TextCommand {
    private TextEditor editor;
    private String textToAdd;
    public AddTextCommand(TextEditor editor, String textToAdd) {
        this.editor = editor;
        this.textToAdd = textToAdd;
    }
    @Override public void execute() {
        editor.addText(textToAdd);
    }
    @Override public void undo() {
        editor.deleteLast(textToAdd.length());
    }
}

class DeleteTextCommand implements TextCommand {
    private TextEditor editor;
    private int charsToDelete;
    private String deletedText = "";

    public DeleteTextCommand(TextEditor editor, int charsToDelete) {
        this.editor = editor;
        this.charsToDelete = charsToDelete;
    }

    @Override public void execute() {
        int len = editor.getText().length();
        deletedText = editor.getText().substring(len - charsToDelete);
        editor.deleteLast(charsToDelete);
    }

    @Override public void undo() {
        editor.addText(deletedText);
    }
}

// Invoker with history
class EditorInvoker {
    private Stack<TextCommand> undoStack = new Stack<>();
    private Stack<TextCommand> redoStack = new Stack<>();

    public void execute(TextCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            TextCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            TextCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}

// Usage
public class TextEditorDemo {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        EditorInvoker invoker = new EditorInvoker();

        invoker.execute(new AddTextCommand(editor, "Hello"));
        invoker.execute(new AddTextCommand(editor, " World"));
        System.out.println(editor.getText()); // "Hello World"

        invoker.undo();
        System.out.println(editor.getText()); // "Hello"

        invoker.redo();
        System.out.println(editor.getText()); // "Hello World"
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Command objects become too heavy | Keep commands lightweight |
| Memory leak with history | Implement max history size |
| Undo not supported | Always implement both execute and undo |
| Tight coupling to receiver | Command should only know interface |

## 🎯 Related Interview Questions

1. **Design a text editor with undo/redo**
2. **Design a job queue / task scheduler**
3. **Design a macro recording system**
4. **Design a transaction rollback mechanism**

## 🆚 Command vs Strategy

| Aspect | Command | Strategy |
|--------|---------|----------|
| Purpose | Encapsulate request | Encapsulate algorithm |
| Focus | What to do + who does it | How to do it |
| Method | execute() | algorithm() |
| Example | Undo/redo, job queue | Payment method, sorting |