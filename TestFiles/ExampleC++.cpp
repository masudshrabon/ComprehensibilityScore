#include <iostream>
#include <vector>
#include <string>
#include <thread>
#include <atomic>
#include <mutex>
#include <memory>
#include <type_traits>

#define PI 3.14

// inline namespace
inline namespace demo_ns {
    constexpr int square(int x) { return x * x; }
}

// consteval function
consteval int getFive() { return 5; }

constexpr int factorial(int n) {
    return n <= 1 ? 1 : n * factorial(n - 1);
}

template<typename T>
concept Number = std::is_arithmetic_v<T>;

template<Number T>
T add(T a, T b) {
    return a + b;
}

struct Base {
    virtual void whoami() { std::cout << "Base\n"; }
    virtual ~Base() noexcept = default;
};

class Derived final : public Base {
public:
    void whoami() override { std::cout << "Derived\n"; }
};

class MyClass {
private:
    mutable int x = 10;
    static int count;
protected:
    enum class Level { Low, Medium, High };
public:
    explicit MyClass(int val) : x(val) {}
    [[nodiscard]] int getValue() const noexcept { return x; }
    friend void printValue(const MyClass& obj);
};

int MyClass::count = 0;

void printValue(const MyClass& obj) {
    std::cout << "Value: " << obj.x << "\n";
}

union Data {
    int intValue;
    float floatValue;
    char charValue;
};

namespace example {
    using namespace std;
    void demonstrate() {
        auto ptr = make_unique<MyClass>(42);
        printValue(*ptr);

        try {
            throw runtime_error("Error occurred");
        } catch (const exception& e) {
            cerr << e.what() << '\n';
        }

        // thread example
        thread t([] {
            this_thread::sleep_for(chrono::milliseconds(100));
            cout << "Thread executed\n";
        });
        t.join();
    }
}

int main() {
    if constexpr (getFive() == 5) {
        std::cout << "Constexpr works!\n";
    }

    static_assert(getFive() == 5, "getFive must return 5");

    register int r = 10;  // deprecated but still a keyword
    volatile int v = 20;

    goto label;

label:
    switch (r) {
        case 10: break;
        default: break;
    }

    for (int i = 0; i < 3; ++i) {
        continue;
    }

    int* arr = new int[5];
    delete[] arr;

    bool flag = true;
    while (flag) {
        flag = false;
        break;
    }

    do {
        int temp = 0;
    } while (false);

    const int ci = 100;
    unsigned long ul = 123456UL;

    Data d = { .intValue = 123 };
    std::cout << "Union int value: " << d.intValue << "\n";

    example::demonstrate();

    return 0;
}
