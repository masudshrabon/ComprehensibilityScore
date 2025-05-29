// JavaScript Code Demonstrating All Keywords

"use strict"; // Enforce strict mode

// 1. Declaration keywords
let a = 1;
const b = 2;
var c = 3;
function myFunc() {}
class MyClass { constructor() {} }
import * as fs from 'fs'; // Dummy import for syntax; not executed in browser
export default MyClass;

// 2. Control flow
if (a < b) {
    for (let i = 0; i < 3; i++) {
        switch (i) {
            case 0:
                continue;
            case 1:
                break;
            default:
                throw new Error("Unexpected value");
        }
    }
} else if (a === b) {
    while (a < 5) {
        a++;
    }
} else {
    do {
        a--;
    } while (a > 0);
}

// 3. Logical operations
try {
    let res = a && b || c ?? 10;
    if (res !== null) {
        delete res.prop;
    }
} catch (e) {
    console.log("Caught:", e);
} finally {
    console.log("Finally block");
}

// 4. Function and generator
async function asyncFunc() {
    await new Promise(resolve => resolve());
}
function* genFunc() {
    yield 1;
}

// 5. Others (use in context)
with (Math) {
    let pi = PI;
}

let obj = {
    get value() { return this._val; },
    set value(v) { this._val = v; }
};

debugger; // Triggers a breakpoint

// 6. Reserved but rarely used directly
enum Color { Red, Green, Blue } // Not valid in JS without transpiler like TypeScript
// implements, interface, package, private, protected, public are reserved for future use

// 7. Strict mode reserved identifiers (some used in classes)
class Example {
    static staticMethod() {}
    #privateField = 1;
    constructor() {
        super(); // used with inheritance
    }
}

// 8. Meta programming
new MyClass();
typeof a;
instanceof MyClass;
in a;

let p = null;
p ??= "fallback";

// List of JS Keywords used (total ~64):
// await, break, case, catch, class, const, continue, debugger, default, delete, do,
// else, enum, export, extends, false, finally, for, function, if, import, in, instanceof,
// new, null, return, super, switch, this, throw, true, try, typeof, var, void, while,
// with, yield, let, static, get, set, async, await

// Future reserved:
// implements, interface, package, private, protected, public, enum (TS only)

