# Uses all Python keywords in one script

import keyword
from math import sqrt as square_root
from sklearn import gaussian_process
class Demo:
    def __init__(self):
        self.value = None
        self.active = True

    def compute(self, x: int):
        assert x >= 0, "Only non-negative numbers allowed"

        def inner(y):
            nonlocal x
            return x + y

        try:
            result = inner(5)
        except Exception as e:
            raise e
        else:
            if result > 10 and result != 15 or not result == 11:
                pass  # just a dummy operation
        finally:
            self.value = result

        return result

def generator():
    yield from (i*i for i in range(5) if i % 2 == 0)

async def async_task():
    await dummy()

async def dummy():
    return True

def check_keywords():
    global demo_instance
    for word in keyword.kwlist:
        if word in globals():
            print(f"{word} is defined in global scope")

if __name__ == "__main__":
    demo_instance = Demo()

    for i in range(3):
        if i == 1:
            continue
        elif i == 2:
            break
        else:
            print("Computing:", demo_instance.compute(i))

    with open("temp.txt", "w") as f:
        f.write("Python keywords example")

    loop_condition = True
    while loop_condition is True:
        loop_condition = False

    lambda_func = lambda a: a * 2
    print(lambda_func(10))

    result = [x for x in generator()]
    print("Generated squares:", result)

    # Del demo_instance from global scope
    del demo_instance
# Uses all Python keywords in one script

import keyword
from math import sqrt as square_root

class Demo:
    def __init__(self):
        self.value = None
        self.active = True

    def compute(self, x: int):
        assert x >= 0, "Only non-negative numbers allowed"

        def inner(y):
            nonlocal x
            return x + y

        try:
            result = inner(5)
        except Exception as e:
            raise e
        else:
            if result > 10 and result != 15 or not result == 11:
                pass  # just a dummy operation
        finally:
            self.value = result

        return result

def generator():
    yield from (i*i for i in range(5) if i % 2 == 0)

async def async_task():
    await dummy()

async def dummy():
    return True

def check_keywords():
    global demo_instance
    for word in keyword.kwlist:
        if word in globals():
            print(f"{word} is defined in global scope")

if __name__ == "__main__":
    demo_instance = Demo()

    for i in range(3):
        if i == 1:
            continue
        elif i == 2:
            break
        else:
            print("Computing:", demo_instance.compute(i))

    with open("temp.txt", "w") as f:
        f.write("Python keywords example")

    loop_condition = True
    while loop_condition is True:
        loop_condition = False

    lambda_func = lambda a: a * 2
    print(lambda_func(10))

    result = [x for x in generator()]
    print("Generated squares:", result)

    # Del demo_instance from global scope
    del demo_instance
