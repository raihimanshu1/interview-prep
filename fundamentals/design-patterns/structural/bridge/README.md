# Bridge Pattern

> **Decouples an abstraction from its implementation so that the two can vary independently.**

## 📖 Concept

**Real-world analogy:** Remote controls and devices. The same remote (abstraction) can work with a TV, Sound System, or Projector. The remote and device can evolve independently.

## 🔍 When to Use

- Want to avoid permanent binding between abstraction and implementation
- Both abstraction and implementation should be extensible via subclassing
- Changes in implementation shouldn't affect client code
- Want to share implementation among multiple objects

## ✅ Interview Checklist

- [ ] Implementor interface defines implementation interface
- [ ] Concrete Implementors implement the interface
- [ ] Abstraction holds reference to Implementor
- [ ] Refined Abstractions extend Abstraction
- [ ] Client depends only on Abstraction

## 🧪 Common Interview Question

**Problem:** Design a Shape+Color system where shapes (Circle, Square) can be rendered in different colors (Red, Blue). The combination should not create 4 separate classes — use Bridge pattern to decouple.

## 💻 Java Implementation

### 1. Basic Bridge

```java
// Implementor
interface Color {
    String applyColor();
}

// Concrete Implementors
class RedColor implements Color {
    @Override
    public String applyColor() { return "Red"; }
}

class BlueColor implements Color {
    @Override
    public String applyColor() { return "Blue"; }
}

// Abstraction
abstract class Shape {
    protected Color color; // bridge to implementation

    public Shape(Color color) {
        this.color = color;
    }

    abstract String draw();
}

// Refined Abstractions
class Circle extends Shape {
    public Circle(Color color) { super(color); }

    @Override
    public String draw() {
        return "Circle drawn in " + color.applyColor();
    }
}

class Square extends Shape {
    public Square(Color color) { super(color); }

    @Override
    public String draw() {
        return "Square drawn in " + color.applyColor();
    }
}
```

### 2. Usage

```java
public class BridgeDemo {
    public static void main(String[] args) {
        // Without Bridge: need 4 classes (RedCircle, BlueCircle, RedSquare, BlueSquare)
        // With Bridge: 2 shapes × 2 colors = composition
        Shape redCircle = new Circle(new RedColor());
        Shape blueSquare = new Square(new BlueColor());

        System.out.println(redCircle.draw()); // Circle drawn in Red
        System.out.println(blueSquare.draw()); // Square drawn in Blue
    }
}
```

### 3. Full Working Example: Device + Remote Control

```java
// Implementor - Device
interface Device {
    void turnOn();
    void turnOff();
    void setChannel(int channel);
}

// Concrete Implementors
class TV implements Device {
    private int channel;

    @Override
    public void turnOn() { System.out.println("TV turned ON"); }
    @Override
    public void turnOff() { System.out.println("TV turned OFF"); }
    @Override
    public void setChannel(int channel) {
        this.channel = channel;
        System.out.println("TV channel set to " + channel);
    }
}

class Radio implements Device {
    private int channel;

    @Override
    public void turnOn() { System.out.println("Radio turned ON"); }
    @Override
    public void turnOff() { System.out.println("Radio turned OFF"); }
    @Override
    public void setChannel(int channel) {
        this.channel = channel;
        System.out.println("Radio frequency set to " + channel);
    }
}

// Abstraction - Remote
abstract class RemoteControl {
    protected Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void turnOn() { device.turnOn(); }
    public void turnOff() { device.turnOff(); }
    public abstract void setChannel(int channel);
}

// Refined Abstractions
class BasicRemote extends RemoteControl {
    public BasicRemote(Device device) { super(device); }

    @Override
    public void setChannel(int channel) {
        device.setChannel(channel);
    }
}

class AdvancedRemote extends RemoteControl {
    public AdvancedRemote(Device device) { super(device); }

    @Override
    public void setChannel(int channel) {
        System.out.println("AdvancedRemote: Selecting channel " + channel);
        device.setChannel(channel);
    }

    public void nextChannel() {
        System.out.println("AdvancedRemote: Next channel");
        device.setChannel(2); // simplified
    }

    public void previousChannel() {
        System.out.println("AdvancedRemote: Previous channel");
        device.setChannel(1); // simplified
    }
}

// Usage
public class DeviceDemo {
    public static void main(String[] args) {
        Device tv = new TV();
        Device radio = new Radio();

        RemoteControl tvRemote = new AdvancedRemote(tv);
        tvRemote.turnOn();
        tvRemote.setChannel(5);

        RemoteControl radioRemote = new BasicRemote(radio);
        radioRemote.turnOn();
        radioRemote.setChannel(101);
        ((AdvancedRemote) tvRemote).nextChannel();
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Too many interfaces | Accept for type safety; simpler cases may not need Bridge |
| Implementor becomes fat | Keep it focused on one responsibility |
| Bridge leaks to client | Client should only use Abstraction |
| Over-engineering simple cases | Use only when you see class explosion |

## 🎯 Related Interview Questions

1. **Design a Device+Remote system** — Remote (Basic/Advanced) × Device (TV/Radio/SoundSystem)
2. **Design a Message+Sender system** — Message (Text/Email) × Sender (SMTP/SMS/Push)
3. **Difference between Bridge and Adapter?** — Bridge is designed upfront; Adapter retrofits existing code

## 🆚 Bridge vs Adapter

| Aspect | Bridge | Adapter |
|--------|--------|---------|
| Purpose | Decouple abstraction from implementation | Make incompatible interfaces work |
| Designed | Upfront | To fix existing incompatibility |
| Direction | Abstraction → Implementor | Client → Adapter → Adaptee |
| Example | Shape + Color, Remote + Device | Legacy payment to new system |