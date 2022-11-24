# Documentation

This page is a step-by-step guide to get you started with Arucas, showing you how to install the language, and how to write code for the language, showing you what is possible in the language as well as its features.

> #### [Installation](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/1.%20Installation.md)
> #### [Development Environment](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/2.%20Development%20Environment.md)

> #### [Syntax](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/3.%20Syntax.md)
> #### [Comments](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/4.%20Comments.md)
> #### [Literals](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/5.%20Literals.md)
> #### [Variables](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/6.%20Variables.md)
> #### [Output](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/7.%20Output.md)
> #### [Input](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/8.%20Input.md)
> #### [Operators](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/9.%20Operators.md)
> #### [Scopes](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/10.%20Scopes.md)
> #### [Conditional Statements](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/11.%20Conditional%20Statements.md)
> #### [Switch Statements](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/12.%20Switch%20Statements.md)
> #### [Loops](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/13.%20Loops.md)
> #### [Functions](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/14.%20Functions.md)
> #### [Members](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/15.%20Members.md)
> #### [Lists](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/16.%20Lists.md)
> #### [Maps](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/17.%20Maps.md)
> #### [Errors](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/18.%20Errors%20(Incomplete).md)
> #### [Imports](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/19.%20Imports%20(Incomplete).md)
> #### [Classes](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/20.%20Classes%20(Incomplete).md)
> #### [Enums](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/21.%20Enums%20(Incomplete).md)
> #### [Threads](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/22.%20Threads%20(Incomplete).md)
> #### [Java](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/23.%20Java%20Integration.md)


## Installation

This installation guide is for those who want to just run vanilla Arucas not bundled in with another application. If you are running Arucas inside another application you can just skip this part.

First you need to install the latest version of Arucas, you can download the jar file from [here](https://github.com/senseiwells/Arucas/releases). 
After downloading the jar make sure you have Java 16 or above installed as Arucas relies on this, you can then run the jar using the command line, replacing `<version>` with the appropriate version:
```
java -jar Arucas-<version>.jar -noformat
```
Now you will be running the Arucas Interpreter, here you can type any Arucas code and it will be run, if you want to exit the interpreter you can simply type:
```
exit
```
To run a file with Arucas code from the command line you can use the Built-in function:
```kotlin
run("path/of/arucas/file.arucas");
```


## Development Environment

We recommend the use of the [Arucas Plugin](https://github.com/Kariaro/ArucasHighlighter/tree/main) designed for IntelliJ by [HardCoded](https://github.com/Kariaro), this highlights your code informing you of errors in your code, and adding nice colours :).

Alternatively, if you do not wish to use IntelliJ another option is to use VSCode and set the language to `Java`, and disable validation for error highlighting. You can also configure VSCode to automatically recognize `.arucas` files as Java. 

So now that you are able to run Arucas files, what do we put inside? If you have not already, you should take a look at the [Language Syntax](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/3.%20Syntax.md) page briefly, but we will cover everything in detail here.


# Language Syntax

This section has a quick rundown of all the syntax that the language has, like a cheat sheet. You may want to refer back to this section after reading other sections.

## Literals

Arucas provides 6 object types that you are able to create with literals, these are:

`Number` - An object containing a floating point number, represented by a number

`String` - An object containing an array of characters, represented by any text within two double or single quote marks

`Boolean` - An object containing only one of two possible values, represented by `true` or `false` 

`List` - An object containing a list of other objects, represented by having object separated with comma's within square brackets

`Map` - An object containing a list of key and value pairs of objects, represented by having a list of pairs of objects separated by colons within curly brackets

`Null` - An object representing nothing, represented by `null`

## Basic Operators:

`=` - This assigns a value to a variable

`+` - This adds two numbers together or concatenates two strings

`-` - This subtracts one number from another

`*` - This multiplies one number from another

`/` - This divides one number by another

`^` - This multiplies one number by itself a number of times

`++` - This increments the value of a number by one

`--` - This decrements the value of a number by one

`.` - This allows you to access members of a value

## Logical Operators:

`&&` - This is the logical AND

`||` - This is the logical OR

`!` - This is the logical NOT

`~` - This is the logical XOR

`==` - This evaluates whether two objects are equal

`>` - This evaluates whether a number is more than another

`<` - This evaluates whether a number is less than another

`>=` - This evaluates whether a number is greater than or equal to another

`<=` - This evaluates whether a number is less than or equal to another

One thing to note for AND and OR is that the left side will always be evaluated first, and if it does not evaluate to `true` in the case of AND, or `false` in the case of OR, the right side will not be evaluated.

Another thing to note is that custom classes are able to override some of these operators, changing their functionality, more information will be on this in the classes section.

## Bitwise Operators:

`&` - This is the bitwise AND

`|` - This is the bitwise OR

`~` - This is the bitwise XOR

`>>` - Right bit shift

`<<` - Left bit shift

One thing to note is that the bitwise AND and OR also work for Booleans, however they do not short circuit, so in the case of AND if the left side was evaluated to `false` it would still evaluate the left side, and similarly OR will evaluate the right side even if the left was already `true`.

## Comments:

You are able to make comments in your code that the compiler will ignore.

`//` - Used to comment until a line break

`/* */` - Used for multi-line comments

## Keywords:


### `{` and `}` 

- These are used to define scopes, or code blocks
- Scopes can contain multiple lines of code inside then and usually is indented to visually show it's in a different scope. Any variables initialized inside a scope cannot be accessed outside that scope, but variables defined outside the scope can be accessed and assigned inside that scope, scopes can be used independently as well as with other statements.

Example:
```kotlin
outside = 9;
{
    // variable only defined inside this scope
    inside = 10;
    // variable defined outside of the scope can be assigned and accessed
    outside = outside - 2;
}
// this would crash as 'inside' doesn't exist outside the scope
outside = inside;
```

### `fun` 

- This is used for defining functions
- Functions can have parameters, however functions outside of classes cannot be overloaded, overloaded functions can not be delegated. Functions are called by having their name followed by two brackets.

Examples:
```kotlin
fun doSomething() {
    print("something");
}

fun doSomethingElse(foo) {
    print(foo);
}

doSomething();
```

A thing to note is that the language is not fussy with how you format your code, in terms of new lines and spaces. So formatting like this is considered valid:

```kotlin
fun doSomething()
{
    if (true) print("");
}
```

### `return` 

- This is for escaping out of a function or file
- You are able to return a value, however this is not required. If there is no return statement in a function it will automatically return `null`, you can also return a value out of a file.

Examples:
```kotlin
fun doSomething() {
    return "something";
}

return doSomething();
```

### `if`, `else if`, and `else` 

- These are for evaluating boolean expressions
- You cannot have `else if`s and `else` without an `if`, if one of these has an expression evaluates to true the code inside their code block will be executed, and the rest will be ignored.

Examples:
```kotlin
if (false) { 
    print("foo"); 
} 
else if (false) { 
    print("bar"); 
} 
else { 
    print("baz"); 
}
```

### `switch`, `case`, and `default` 

- These are for matching values
- Switch statements can compare a value to both literals and other expressions, similar to an if statement, you cannot have two of the same literal in the cases and the switch statement is evaluated from first case down to last case with literals being the first thing that is checked in each case. If the value does not match any cases, it will run the default branch. You cannot have more than one default branch.

Example:
```kotlin
fun getNum() {
    return 1;
}

number = 1;
switch (number) {
    // Even though getNum comes before 1 the value
    // will check against literals first, so in this
    // case getNum will ne
    case getNum(), 1, 2, 3 -> {
        print("low");
    }
    case 4, 5 -> { }
    case 6 -> {
        print("highest");
    }
    default -> {
        print("unkown");
    }
}
```

### `while` 

- This is for creating a simple loop
- `while` evaluates a boolean expression similar to `if`, and if it evaluates to `true` then it will execute the code inside, and then go back to the top and repeat the process until it evaluates to `false`.

Examples:
```kotlin
while (true) {
    print("This is an infinite loop");
}

i = 0;
while (i < 10) {
    print(i);
    i++;
}
```

### `for` 

- This is for creating an iteration loop
- `for` takes in three expressions, the first will only be executed once at the start, the second is similar to the condition in a while loop, and the third gets executed every time the `for` loop completes.

Example:
```kotlin
/* 
 * This example is exactly the same as the previous 
 * while loop example, just more consise
 */
for (i = 0; i < 10; i++) {
    print(i);
}

list = [4, 5, 2, 7];
for (i = 0; i < len(list); i++) {
    value = list.get(i);
    print(value);
}
```

### `foreach` 

- This is for iterating over the values in a list
- `foreach` this is similar to the Java enhanced for loop, you have an identifier which will hold your value and the desired list that you want to iterate over.

Example:
```kotlin
// this does the same as the previous for loop example, just more consise
list = [4, 5, 2, 7];
for (value : list) {
    print(value);
}
```

### `continue` 

- This allows you to return to the top of a loop

Example:
```kotlin
for (i = 0; i < 10; i++) {
    if (i == 3) {
        continue;
    }
    print(i);
}
```

### `break` 

- This allows you to escape a loop or case statement

Examples:
```kotlin
for (i = 0; i < 10; i++) {
    if (i == 3) {
        break;
    }
    print(i);
}

number = 18;
switch ("foo") {
    case "bar", "baz" -> {
        print("b");
    } 
    case "foo", "faz" -> {
        if (number >= 18) {
            break;
        }
        print("f");
    }
}
```

### `throw`

- This keyword is used to throw an error
- You can only throw `Error`s, you can construct an `Error` by using the `Error` class, if these go uncaught they will crash the program.

Example:
```kotlin
throw new Error("Something went wrong!", 540);
```

### `try` and `catch` 

- These are used to handle exceptions
- `catch` can only catch `Error`s, the caught value will be of type `Error` which encapsulates the error message and possibly a value. If errors go uncaught, they will crash the program.

Example:
```kotlin
try {
    throw null;
}
catch (error) {
    print(error);
}
```

### `class` 

- This is used to define a `class`
- Classes allow for encapsulation, wrapping other objects (values) into a single object, this class defines that single object and what that object is able to do. 
- If a class has no constructors, you will be able to construct that class with no parameters. Classes also allow you to define methods, or class functions.  You can overload constructors, and methods in a class by differing the number of parameters.
- You can initialize the class members in the constructor, or initialize them when you define them. If you do not define them, they will default to being `null`.

Example:
```kotlin
class Example {
    var other1;
    var other2 = 10;

    Example() { }

    Example(param) {
        this.other1 = param;
    }
}
```

### `var` 

- This is used for declaring variables inside classes
- The var keyword can be used for `static` and non-`static` variables.
- These variables you define can be access using the `.` operator

Examples:
```kotlin
class Example {
    var pi = 3.14;
    var name = "sensei";
}
```

### `.` 

- This is used to access members of an object
- This can be used on `class` names for `static` members or on an object for the object members.

Example:
```kotlin
class Example {
    var number = 10;
    
    // If there is no constructor
    // Arucas generates a synthetic one

    fun printNumber() {
        print(this.number);
    }
}

// We are able to construct it with no parameters
example = new Example();

// these two do the same thing
print(example.number);
example.printNumber();
```

### `this` 

- This is used for referring to itself inside a class
- `this` is passed in internally, and you will have access to it inside a class constructor, method, or operator method, `this` allows you to refer to all the members inside your class, you can assign, access, or call them.

Example:
```kotlin
class Example {
    var number = 10;

    fun setNumber(newNumber) {
        this.number = newNumber;
    }

    fun resetNumber() {
        this.number = 10;
    }
}

e = new Example();
// When we call a method on a value
// it internally passes itself as a parameter
// so you could think of it as resetNumber(e)
// where inside the resetNumber function
// e is referenced with the this keyword
e.resetNumber();
``` 

### `new` 

- This is used to construct a new object
- This is used for custom classes, as the built-in objects cannot be constructed, as they have literals. This will return a new instance of that object.

Examples:
```kotlin
class Example {
    var number = 10;
    
    // We have multiple constructors
    Example() { }
    
    Example(number) {
        this.number = number;
    }
}

// Construction
defaultExample = new Example();
myExample = new Example(20);
```

### `operator` 

- This allows you to override operator methods for an object
- An important thing to note about this is that it will be the left side's operator method that will get used, for example `a + b` would result in `a`'s plus the operator getting called not `b`'s. Whereas `b + a` would result in `b`'s plus operator getting called not `a`'s
- The operators that you can override are: `+`, `-`, `*`, `/`, `^`, `<`, `<=`, `>`, `>=`, `==`, and `!`

Examples:
```kotlin
class Example {
    var number = 10;

    operator + (other) {
        return this.number + other;
    }

    operator ! () {
        return this.number * -1;
    }

    operator == (other) {
        return this.number == other;
    }
}

// Constructing the class using the new keyword
example = new Example();

plusExample = example + 10; // this would return 20
notExample = !example; // this would return -10
equalsExample = example == 10; // this would return true

// this would throw an error, because 10's operator is being called not example's
errorExample = 10 + example; 
``` 

### `static` 

- This lets you define `static` members, methods, and initializers in a class
- `static` members and methods can be access without an instance of the class. And a `static` initializer will run when the class is evaluated, `static` methods can be overloaded. `static` members are access by typing the `class` name followed by a `.` and then the member name.

Example:
```kotlin
class Example {
    static var number = 10;

    static {
        print("Example class was loaded");
    }

    static fun print() {
        print(Example.number);
    }

    static fun print(prefix) {
        print("%s %s".formatted(prefix, Example.number)); 
    }
}
```

`enum`

- Enums are somewhat similar to classes, but they cannot be constructed and instead have static-like variables of the type that cannot be modified. Enums are great if you want constants that can also encapsulate other values.

Example:
```kotlin
// Every enum by default has 2 static functions
// Direction.values() and Direction.fromString(str)
// they get all the values of the enum and convert
// a string to a enum by name respectively. 
enum Direction {
    NORTH("North"),
    // You can have brackets if you want
    SOUTH(),
    // Or not
    EAST,
    WEST("Wow");

    Direction() {
        // All enum values by default have
        // 2 methods, getName and ordinal
        // getting the name of the enum and the
        // index of the enum respectively
        this.prettyName = this.getName();
    }
    
    /* Constructors are used when initialising
     * the enum values, they can take any values
     */
    Direction(prettyName) {
        this.prettyName = prettyName;
    }

    /* Not a good way to do this, should have
     * opposite as a member variable but this is
     * just an example.
     */
    fun getOpposite() {
        switch(this) {
            case Direction.NORTH -> return Direction.SOUTH;
            case Direction.SOUTH -> return Direction.NORTH;
            case Direction.EAST -> return Direction.WEST;
            case Direction.WEST -> return Direction.EAST;
        }
    }
}

// Direction.NORTH = "Random Value!"; <-- This would crash
// direction = new Direction(); <-- This would also crash
print(Direction.NORTH.getOpposite());
```

`import` and `from`

- These keywords allow you to import classes from other files.
- Currently, cyclical imports are not supported, but usually you don't need two files importing each other.

```kotlin
// Imports all classes from util.Internal file
import * from util.Internal;
// Only imports Collector class from util.Collection
import Collector from util.Collection;
// If you want to import your own file you must have it
// inside your libraries folder, in Vanilla arucas this
// is Users/user/.arucas/libs/

// Since we are importing the Collector class we can not use it
collector = Collector.of(1, 2, 3, 4, 5, 6);
list = collector.filter(fun(value) { return value > 4; }).toList();
print(list);
```

## Comments

Let's start by introducing comments, comments don't do anything in terms of running the code, but instead they allow you to describe what is happening in the code, when the code is run all comments are completely ignored.

There are two types of comments, single line and multiline. A single line comment can be written by typing `//` followed by your text, and a multi line comment consists of `/*` followed by your line(s) of text and then closed with `*/`:
```
// This is a single line comment
/*
 This is a multi line comment
 Very cool!
 */
```
An important thing to note about single line comments is that text will only be ignored after the `//`, anything on the left of the comment will still run as code.

## Literals

### String literals

Creating a string is similar to all other languages, you can create a string of characters by using double quotes, `""`, or single quotes, `''`, this is personal preference as there is no difference.
```kotlin
"Example string"
'Example string'
```
You are also able to escape certain characters by using the `\` character, this is to be able to use `"`, `'`, and other characters like tab `\t`, and newline `\n` inside of strings.
```kotlin
"\tIntended example with new line\n"
```

Now if you tried to just have a string literal in an Arucas file it would throw an error, this is because all code expressions must be followed by a `;`, this is how the language is able to know when one expression ends and another starts.
```kotlin
"This is valid syntax!";
```

### Number literals

Numbers are very easy to create, you can simply just type them! Numbers can be easily modified and are an essential value, we will explore more about how to manipulate numbers in the operators section.
```kotlin
0; 1; 2; 3; 4; 5;
```
Decimals are also supported:
```kotlin
1.5; 3.1415926; 
```
You are also able to write numbers in hexadecimal (base 16), don't worry if you don't know what this is, it's not necessary to use, just a handy feature.
```kotlin
0xFF; 0x1B9E00;
```

### Boolean literals

Booleans are very simple, there are just two possible literals for these.
```kotlin
true;
false;
```
These values are used to do boolean logic, which we will cover in the operators section.

### Null literal

Null is as simple as it gets, there is only one literal for it:
```
null;
```
The `null` value represents nothing, it is used when a value doesn't exist, be careful will how it is used though, null safety is important, you don't want to get `null` values where you want other values.

### List literals

Lists are a more complex data structure and these allow you to store many values of any type inside of it, including other lists. Lists in Arucas have a very simple syntax:
```kotlin
["Example", 1, true, null];
```
Lists are great for storing many pieces of data in one place. We will cover lists more in detail later in this guide.

### Map literals

Maps are also a complex data structure that allow you to map one value to another, allowing you to make keys to access values. The syntax is very straightforward:
```kotlin
// Here we are mapping numbers to their names
{1: "one", 2: "two", 3: "three"};
```
Maps are a fast way of storing data that needs to be accessed, again we will cover maps in greater detail later.


## Variables

A key part of programming is being able to manipulate data, and we do this by using variables, you can think of a variable like a container. To define a variable we need to give it a name and then a value, and we can assign the variable with the value by using the assignment operator, `=`:
```kotlin
exampleVariable = "Example string";
```
Assigning a variable is like putting something inside the container.

Variable names can only include letters and underscores, by convention variable names should follow camel casing, this is where you capitalize all the words bar the first then squash them together.

Once you have defined a variable, you can reassign the variable by again using the assignment operator.
```kotlin
exampleVariable = "Example string";
exampleVariable = "Overwritten!";
// exampleVariable now stores the value: "Overwritten"
```
Variables can store any type of value, we will come onto other types of values, for example numbers or booleans.

Now once you have stored a value in a variable you can use it by referencing the name of the variable, referring back to the previous analogy, this is like peeking into the container to see what is inside.
```kotlin
exampleVariable = "Example string";
print(exampleVariable);
// We would get an output of: Example string
```


## Output

Now we know how to create a string, we can output it to the console. We can do this by using a function, we will cover functions in more detail later, but for now we can just use it and accept that it works. The main function that you will use to output is called `print`, and to call the function we follow the name up with a pair of brackets:
```kotlin
print();
```
This won't actually print anything, since we haven't told it what to print. We can provide this information by adding arguments inside our brackets:
```kotlin
// Having 1 parameter in the print function causes 
// it to automatically add a new line after it
print("Hello World!");
// This would print: Hello World\n
```
The `print` function also has the capability of concatenating (joining) strings together
```kotlin
print("Hello", "World");
// This would print: Hello World
```

## Input

We can take input from the console, by using another simple function called `input`, unlike print this function can only have one parameter, this is the prompt that the user is displayed with for their input:
```kotlin
input("What is your name? ");
```
The user will then be able to type in the console and once they press enter their input will be submitted.

So now that we've got the function to prompt the user with input we need to store it, and we can do this by using a variable, like before how we stored literals inside a variable we can store what we call the return value of the function in a variable too:
```kotlin
userInput = input("What is your name? ");
```
Now that we have the user input stored in a variable, we can use it inside our code:
```kotlin
userInput = input("What is your name? ");
// If the user inputted "mike"
print("Your name is: ", userInput, "\n");
// This should print Your name is: mike\n
```

## Operators

There are quite a few operators in Arucas but don't worry, most of them are similar to other languages and are easy to pick up!

### `(` and `)` - Brackets

While not necessarily an operator, I think that brackets are an important thing to mention before we talk about the other operators. Similar to what you might have learnt in maths, brackets allow you to change the order of operations. We will cover where brackets may be useful in the following operators.

### `=` - Assignment

We have already covered this operator, the assignment operator, briefly when talking about creating variables, but I will reiterate, this operator allows you to assign a value to a variable:
```kotlin
exampleVariable = "Example string";
```
The assignment operator also has a neat feature that allows you to assign multiple values at the same time using lists:
```kotlin
example1, example2, example3 = [1, 2, 3];
print(example1); // This would print 1
print(example2); // This would print 2
print(example3); // This would print 3
```
This works with any list but the list must be the same size as the number of values you want to assign to:
```kotlin
// This would crash since there is not a match
// with the number of variables and values
example1, example2 = [1, 2, 3];
```

Another thing that is important about assignment is that it will also return the value that was just assigned, for example:
```kotlin
v = 10;
print(v = 12);
// This will print 12 because the assignment
// returns the value that was assigned which was 12
```
A mention about brackets here, the value being assigned will always be evaluated first so for example:
```kotlin
v = 0;
// We will cover the addition operator in the next
// section you might want to read that first then come back
print(v = 10 + 3);
// This will print 13 because v was assigned
// to the value of 13 because 10 + 3 = 13
```
But what if we want to assign a value to a variable, then manipulate that value to print:
```kotlin
v = 0;
// We put the assignment in brackets so it
// happens with out adding the 3 to the 10
print((v = 10) + 3);
// This will print 13 because v was assigned to 10
// and then the assignment returned the value which 
// in this case was 10, then it added 3 to that value
// which equals 13 which was then printed

print(v);
// This will print 10 since we did not assign 13 to v
``` 

### `+` - Addition

This is the addition operator, this allows you to add two things together, usually numbers, but this operator by default also works with strings to concatenate them.
```kotlin
result = 9 + 10;
print(result); 
// This would print 21... I mean 19

print(0.5 + 0.5);
// This would print 1

stringResult = "5" + "6";
print(stringResult);
// This would print 56

print("Hello W" + "orld");
// This would print Hello World 
```

The addition operator can also be used as a unary operator, this means that you can have it on the left side of a value with no other value on the left, like this:
```kotlin
print(+10);
// This would print 10
```
This is pretty redundant, but is to be consistent with the subtraction unary operator.

### `-` - Subtraction

This is the subtraction operator, this allows you to take away one value to another, by default this operator only works with numbers.
```kotlin
someMath = 29 - 8;
print(someMath);
// This would print 21

print(9 - 80);
// This would print -71
```

The subtraction operator can also be used as a unary operator, this allows you to write negative values:
```kotlin
print(-10);
// This would print -10
```
An important thing to note is that the subtraction operator has a low predecence and so it will be applied last, here is an example:
```kotlin
-2 ^ 2; // -> -4, this does 2 ^ 2 then makes it negative

(-2) ^ 2; // -> 4
```

### `*` - Multiplication

This is the multiplication operator, and this allows you to multiply two values together, by default this only works with numbers.
```kotlin
print(5 * 4);
// This would print 20

print(20 * 0.5);
// This would print 10
```

An important thing to note is that multiplication will take precedence over addition and subtraction, here's an example:
```kotlin
3 + 4 * 5; // -> 23
```
If you want addition to take precedence, then you will need to use brackets:
```kotlin
(3 + 4) * 5; // -> 35
```

### `/` - Division

This is the division operator, this allows for dividing of two values, by default this only works with numbers.
```kotlin
print(20 / 2);
// This would print 10

print(3.141 / 500);
// This would print 0.006282
```

Similar to multiplication, division takes precedence over addition and subtraction.

### `^` - Exponent

This is the exponent operator and allows you to raise a base to a power, by default this only works with numbers.
```kotlin
print(2 ^ 5);
// This would print 32

print(25 ^ 0.5);
// This would print 5
```

Exponents take precedence over both addition, subtraction, multiplication, and division, here's an example:
```kotlin
5 * 2 ^ 3; // -> 40

(5 * 2) ^ 3; // -> 1000
```

### `++` and `--` - Increment and Decrement

These are the increment and decrement operators, by default these only work on numbers, these are just syntactic sugar for making a value equal to one more or less than its current value:
```kotlin
value = 9;
value++; // value now equals 10
value--; // value now equals 9
```

Using the increment and decrement operators is the exact same as writing:
```kotlin
value = 9;
value = value + 1; // value now equals 10
value = value - 1; // value now equals 9
```

Internally, Arucas compiles the first example into the second example. The increment and decrement are just a shorthand.

### `.` - Dot

The dot operator is used to access and call members of a value, don't worry if you don't know what this means, yet we will cover this in more detail. Every value has members by default, and this is how you can interact with them.
```kotlin
value = "Example string";
value = value.uppercase();
// value now equals "EXAMPLE STRING"
```

### `&&` - AND

This is the and operator, and by default is used between boolean values for boolean logic. Here is and example:
```kotlin
true && true; // -> true
true && false; // -> false
false && true; // -> false
false && false; // -> false
```

The and operator takes two boolean values and will only return `true` if both boolean values are `true` otherwise it will return `false`.

An important feature of this and operator is that it short circuits. Now to explain this you need to understand that the expressions are evaluated one at a time, and it goes from left to right. If the left expression of the and operator is `false` then it knows that no matter whether the right-hand side is `true` or fast it will always return `false` so it skips evaluating the right-hand side.

If you want to use an and operator that evaluates both sides you can use the bitwise and operator `&`, we will go over this later.

### `||` - OR

This is the or operator, and by default is used between boolean values for boolean logic. Here is an example:
```kotlin
true || true; // -> true
true || false; // -> true
false || true; // -> true
false || false; // -> false
```
The or operator takes two booleans and will only return `true` if at least one of the boolean values is `true`, otherwise it will return `false`.

Similarly to the and operator, this will short circuit, if the left-hand side evaluates to `true` then it will always return `true` so it skips evaluating the right-hand side.

If you want to use an or operator that evaluates both sides you can use the bitwise or operator `|`, we will again go over this later.

### `~` - XOR

This is the exclusive or operator and can be used with booleans (as well as numbers, but will cover this later) by default. Here is an example:
```kotlin
true ~ true; // -> false
true ~ false; // -> true
false ~ true; // -> true
false ~ false; // -> false
```
This exclusive or operator takes two boolean and will only return `true` if the boolean values are different from each other, in this case one must always be `true`, and one must always be `false` for it to return `true`.

This operator does not short circuit since it always needs to check both left and right-hand side, this is the same operator that is used for the bitwise XOR, we will go over this later.

### `!` - NOT

This is the not operator and by default only can be used for booleans, this inverts the boolean, here is an example:
```kotlin
!true; // -> false
!false; // -> true
```
This takes the boolean and returns the opposite boolean value, unlike the other operators shown this a unary only operator, meaning it only has a value on the right-hand side and not the left.

### `==` - Equals

This is the equals operator and can be used between any values, it checks whether two values are equal.
```kotlin
true == false; // -> false
"string" == "string"; // -> true

num = 10;
num == 10; // -> true
```
This is often useful for doing `null` checks, the safest way to do a null check is the following:
```kotlin
example = null;
example = "";
null == example; // -> false
```

### `>`, `<`, `>=`, and `<=` - Comparison

These are the comparison operators that can be used to see whether values are greater than, less than, greater than or equal, or less than or equal. Be default, this only works with numbers.
```kotlin
9 > 5; // -> true
9 < 5; // -> false
5 >= 5; // -> true
6.5 <= 6.2; // -> false 
```

### `&` - Bitwise AND

This is the bitwise and operator, this works on both booleans and numbers. On booleans it acts similar to the `&&` operator but does not short circuit. On numbers, it compares the bits, here is an example of `420 & 255`
```
110100100 <- 420
011111111 <- 255
--------- &
010100100 <- 164
```
It compares the bits in each position with each other and will only return 1 if both bits in both numbers are 1 in that position.

### `|` - Bitwise OR

This is the bitwise or operator, this works on both booleans and numbers. On booleans it acts similar to the `|` operator but does not short circuit. On numbers, it compares the bits, here is an example of `240 | 14`
```
11110000 <- 240
00001110 <- 14
-------- |
11111110 <- 254
```
It compares the bits in each position with each other and will return 1 if either of the bits at that position is 1.

### `~` - Bitwise XOR

This is the bitwise exclusive or operator, this is the same operator that is used for the boolean XOR previously mentioned, but this can also be used to manipulate bits. Here is an example: `165 ~ 170`
```
10100101 <- 165
10101010 <- 170
-------- ~
00001111 <- 15
```
It compares the bits in each position with eachother and will only return 1 if only 1 of the bits is 1 and the other is 0.

### `>>` and `<<` - Bit shift right and Bit shift left

These are the bit shifting operators, these by default only work on numbers, they work by taking the bits of the number and shifting them left or right by a certain amount.
```kt
255 >> 2; // 11111111 -> 00111111 = 63
64 << 1; // 0100000 -> 10000000 = 128
```

## Scopes

Scopes are sections in your program that you define your code, scopes determine what variables, functions, and classes are accessible, by default the program runs in the global scope where everything is accessible to the rest of the program.

You are able to define scopes by using the `{` and `}`. For example:
```kotlin
// Global scope
{
    // Defined scope
}
```

Anything that is defined in a scope is only accessible to that scope and any scopes inside that scope:
```kotlin
// Global scope
// Anything here is accessible ANYWHERE in the program
// i is in the global scope
i = 10;
{
    print(i); // -> 10
    // j is in a sub scope and cannot be accessed
    // in any parent scope, in this case that would
    // be the global scope
    j = 20;
    {
        print(i); // -> 10
        print(j); // -> 20
        // Both i and j are accessible because this
        // scope has parents with both of these values
    }
}
print(j); 
// This would throw an Error because
// j is not defined in this scope

print(i); // -> 10
```

Assigning variables in scope also works similarly, if a variable is defined in the global scope, and you reassign that variable in a scope than the variables in the global scope will be modified.
```kotlin
i = 0;
{
    i = 10;
}
{
    i = i - 1;
}
print(i); // -> 9
```

## Conditional Statements

Conditional statements allow you to branch your code into different scopes based on a boolean. The keywords that are used for conditional statements are `if` and `else`. It works by evaluating an expression, if it evaluates to true then it will run the scope after the `if` statement, otherwise if there is an `else` after the if then it will run that scope instead:
```kotlin
if (true) {
    // This will always be run
    // since true is always true
}

if (false) {
    // This will never run
    // since false is always not true
} else {
    // This will always run
}
```

Here is a better example:
```kotlin
name = input("What is your name");
if (name == "Sensei") {
    print("Wow that's a very cool name!");
} else {
    print("That's a cool name but not as cool as Sensei!");
}
```

Shorthand syntax, this syntax applies to most statements that have a scope after it.
This allows you to not use the braces after a statement, but only allows you to have one statement inside of it:
```kotlin
// Skipping the braces
// for one statement
if (true) print("That was true");
else print("This is imposible");
```

This shorthand syntax allows use to easily chain these conditional statements to create `else if`:
```kotlin
if (false) {
    // Do something
} else if (true) {
    // Do something else
}

// This above is the same as writing:
if (false); // Do something
else {
    if (true) {
        // Do something else
    }
}
// You are just skipping the braces after else
// since if is only one statement
```

Long chains of `else if`s are not recommended, and instead you should take a look at the `switch` statement which has a much nicer syntax than:
```kotlin
name = input("Name?");
if (name == "Alex") {
    // Alex
} else if (name == "James") {
    // James
} else if (name == "Xavier") {
    // Xavier
} else if (name == "Jenny") {
    // Jenny
}
// ...
```

## Switch Statements

Switch statements allow you to match values with an input, switch statements are faster when comparing literals (String, Number, Boolean, Null) as they can be evaluated at compile time. Switch statements have cases and will match the input to a case, which will then run a scope accordingly. Switch statements cannot have duplicate literals, but can have expressions that are evaluated at run time (like functions):
```kotlin
name = input("Name?");
switch (name) {
    case "Alex" -> {
        // Alex
    }
    case "James" -> {
        // Alex
    }
    case "Xavier" -> {
        // Xavier
    }
    case "Jenny" -> {
        // Jenny
    }
}

switch (name) {
    case input("Name again?") -> {
        // name == input("Name again?")
    }
}
```

Switch statements also have the ability to have multiple values for each case, as well as having a default case which will be run if the input matches none of the cases.
```kotlin
name = input("Name?");
switch(name) {
    case "Alex", "Steve" -> {
        // name == "Alex" || name == "Steve"
    }
    case null -> {
        // name == null
    }
    default -> {
        // Name was not Alex or Steve and was not null
    }
}
```

## Loops

There are different ways of looping in Arucas, they all are similar, but some work better in certain applications.

### `while`

While loops are the simplest form of loops they work by checking a condition then running a section of code, after it has finished running it will return to the condition and check it again, the loop will end when the condition is evaluated to false or if a `break` statement is used inside a loop, but we will cover this later.

Here is a simple example of how you could make an infinite loop that will never end.
```kotlin
// The condition inside this while expression 
// is true, this means it will always be true
// and as a result this loop will never end
while (true) {
}
// This program will never end naturally
```

You can use a while loop to iterate over numbers, here is an example:
```kotlin
counter = 0;
// This will loop until counter >= 10
while (counter < 10) {
    counter++;
    // Increments the counter by 1
}
```

However, this way of iterating can lead to human errors, accidentally missing the increment of the counter would lead to the loop never ending, and so we would more commonly use a `for` loop. 

### `for`

The for keyword is used to define a for loop, similar to the C style loop. The for expression contains 3 sub expressions, the first is evaluated at the start of the loop only, the second is evaluated as the condition for the loop to continue similar to the while loop, and the last gets executed whenever the loop reaches the end.
```kotlin
// Usually define the initial variable in the first expression (i = 0)
// The condition in the second (i < 10)
// Thirdly the expression that gets run at the end of each loop (i++)
for (i = 0; i < 10; i++) {
}
```

Similarly to the while loop you can easily make an infinite for loop, the expressions in the for loop can remain empty allowing you to do something similar to the following:
```kotlin
// No first expression
// Condition is always true
// No final expression
for (; true;) {
}
```

A common use for `for` loops is iterating over the indexes of a list, we haven't covered lists in great detail just yet, but you are welcome to come back here once we have.
```kotlin
list = ["foo", "bar", "baz"];

// Remember indexes start at 0!
for (i = 0; i < len(list); i++) {
    item = list.get(i);
    print(item);
}
```

### `foreach`

Foreach is a developed version of the for loop allowing easier iteration of collections, this could be lists, sets, collectors, etc.
Similar to the previous example in the for loop, but you do not need to define an index to iterate over, you can just simply iterate over each item in the list.
```kotlin
list = ["foo", "bar", "baz"];

foreach (item : list) {
    print(item);
}
```

This is a much simpler way of iterating, something that you should keep in mind is that when iterating over maps, it iterates over the keys in the map, you can then use that to get the value if you wish:
```kotlin
map = {"foo": "oof", "bar": "rab", "baz": "zab"};

foreach (key : map) {
    value = map.get(key);
    print(key);   // -> foo, bar, baz
    print(value); // -> oof, rab, zab
}
```

### `break`

The `break` keyword allows you to break out of a loop at any point, and the loop will no longer be executed further, this cannot break out of nested loops only the most recent loop that you are inside. The break keyword works inside `while`, `for`, and `foreach` loops.
```kotlin
// Same iteration as shown before from 0-9
for (i = 0; i < 10; i++) {
    // If i is greater than 5 we stop and break the loop
    if (i > 5) {
        break;
    }
}
```

### `continue`

The `continue` keyword is similar to the break keyword in that it allows you to disrupt the flow of a loop. Unlike the break keyword however this doesn't terminate the loop, instead it stops the loop and returns it back to the beginning, this works with `while`, `for`, and `foreach` loops.
```kotlin
for (i = 0; i < 10; i++) {
    if (i == 6) {
        // We go back to the start of the loop
        // the final statement in the for loop
        // still gets executed so i increments
        continue;
    }
    print(i); // 0, 1, 2, 3, 4, 5, 7, 8, 9 <- no 6
}
```

### Recursion

Recursion is a type of loop or iteration that works when a function calls itself causing a chain effect, usually the function has a condition where it does not call itself and exits, usually recursion is slower than the other traditional loops and is more unsafe as it can lead to a possibility of the stack overflowing which will lead to it throwing an error.
```kotlin
// This function is unsafe and will result in a stack overflow
fun recurse() {
    // Calls itself
    recurse();
}
```

A more safe approach if you must use recursion is to have a counter that lets the function know how deep it is:
```kotlin
fun recurse(depth) {
    if (depth > 10) {
        print("Depth of 10, stopping...");
        return;
    }
    // Increase the depth ever time we recurse
    recurse(depth + 1);
}
// This is now safe to call, it will only call itself
// Until it hits the depth limit
recurse(0);
```

## Functions

Functions are a great abstraction that we use to hide complexity and easily reuse code, functions allow you to write code that can be executed from elsewhere in your program by referencing the function's name, or identifier. 

### Simple Functions

Functions in Arucas are defined with the `fun` keyword followed by an identifier then brackets which contain your parameters for the function we will cover this more in a moment, then it is followed by some statements in a scope, here is an example:
```kotlin
fun exampleFunction() {
    print("Function was called");
}

// To call a function we use the name of the
// function then brackets to call it
// similar to how we use the print function
exampleFunction(); // prints "Function was called"
```

Now, if we wanted to add some parameters:
```kotlin
// The parameter is a variable that you can
// use inside of your function, in this case
// we take in a name then use it to print a statement
fun anotherFunction(name) {
    print("Your name is " + name + "!");
}

// To call the function with a parameter we 
// just need to do the same as before but
// include what we want to pass into the function
// as the name variable
anotherFunction("sensei"); // prints "Your name is sensei!"
```

You can add more parameters by adding commas and listing all the parameters you wish to take in:
```kotlin
// Parameter names must be different
// so you can differentiate between them in the function
fun moreFunction(number1, number2) {
    // We take both numbers and print the sum of them
    print(number1 + number2);
}

// To call the function we put the parameters
// we want to pass in separated by commas in the
// same order that the function has them
// in this case number1 = 9, number2 = 10
moreFunction(9, 10); // prints 19
```

### Variable Parameters

You are also able to take in a variable amount of parameters, this means that you can call the function with as many parameters as you want, and it is passed into the function as a list. To do this, we define the parameter in the function followed by `...`. 
```kotlin
// The numbers parameter is always a list
// filled in with the parameters
variableParameters(numbers...) {
    total = 0;
    // Since numbers is a list we can iterate
    // over it using a foreach loop
    foreach (num : numbers) {
        total = total + num;
    }
    // This function adds up all the given numbers
    // and then outputs the total
    print(total);
}

variableParameters(1, 2, 3, 4, 9); // prints 19
// numbers = [1, 2, 3, 4, 9]

variableParameters(); // prints 0
// numbers = []

variableParameters(-9); // prints -9
// numbers = [-9]
```

Another important thing to know is that functions are first class objects, meaning that they are treated just like any other value. So you can store functions in variables and pass functions into other functions, which allows you to write more flexible code.
```kotlin
fun exampleFunction() {
    print("Example function was called!");
}

// We don't call the function just reference it by it's name
// so exampleFunction not exampleFunction()
variable = exampleFunction;

// Now since variable stores the exampleFunction function
// we can actually call variable as if it were a function
variable(); // prints "Example function was called!"
```

### Lambdas

You are also able to create anonymous functions or more frequently called lambdas, these functions can be defined on the go and cannot be called like normal functions since they do not exist with a name, but you can still call them through a variable like previously shown, you can define an anonymous function with the `fun` keyword and skipping the identifier and then brackets with the parameters followed by your statements in a scope.
```kotlin
lambda = fun() {
    print("This is a lambda");
};
lambda(); // prints "This is a lambda"
```

Here is a use when you need to pass a function into another function:
```kotlin
fun runFunctionWithDelay(delay, function) {
    // This pauses the program for an amount of milliseconds
    sleep(delay);
    // This calls the function
    function();
}

// In this case our lambda cannot have parameters
// because runFunctionWithDelay doesn't pass in any parameters
runFunctionWithDelay(100, fun() {
    print("Printed after 100 milliseconds");
});
```

### Return Statements

Functions have the ability to return values, functions technically always return values in fact, similar to how the `input` function works which we looked at earlier that returns whatever the user inputted into the console. Return statements can be used inside of functions to return a value, you can do this by using the `return` keyword followed by a value, or alternatively if you don't want to return a value you can just leave it blank, if you leave it blank then the function will by default just return `null`, then follow that by a semicolon:
```kotlin
fun biggerThanFive(num) {
    if (num > 5) {
        return "Bigger than 5";
    }
    return "Smaller than 5";
}

// We need to assign the return value to something
biggerThan5 = biggerThanFive(20); // -> "Bigger than 5"

print(biggerThan5); // prints "Bigger than 5"

// We can also ignore the return value if we don't want it
biggerThanFive(4);
// In this case ignoring the return value is pointless but
// this may be the case of other functions that run code
```

Here is an example where `return` is used to escape a function to stop its execution, return does not always need to return a value and this is a very common use case which can reduce your indentation level which can make your code cleaner and easier to read overall. 
```kotlin
fun printNameIfOver18(name, age) {
    if (age < 18) {
        // Under 18 so we don't want to
        // execute any more of this function
        // so we just return out of the function
        return;
    }
    print(name);
}

// Another way of writing the previous function
fun printNameIfOver18(name, age) {
    // In this case the indentation doesn't matter
    // but when you have long chains it can make a 
    // big difference and so you might want to return
    if (age >= 18) {
        print(name);
    }
}
```

### Function Scoping

Functions capture variables in the previous scopes to be able to use them in the scope, this lets you use variables from previous scopes:
```kotlin
{
    // Inner scope
    inner = "Random String";
    fun doSomething() {
        print(inner);
    }
    
    doSomething();
}
```

However, functions also have a special property where since they capture the whole previous scope that can access variables that are not yet defined:
```kotlin
{
    fun doSomething() {
        // Technically this variable doesn't exist yet
        print(postFunctionVariable);
    }

    // Now it exists
    postFunctionVariable = 10;

    // Now we call the function
    doSomething();
}
```

However, if you call the function before you define the variable the program will throw an error:
```kotlin
{
    fun doSomething() {
        // Technically this variable doesn't exist yet
        print(postFunctionVariable);
    }

    // Now we call the function
    doSomething();
    // This will crash because postFunctionVariable is not defined yet!

    // Now it exists
    postFunctionVariable = 10;
}
```

Here is an example that may seem confusing at first, but understanding how scoping of functions works helps:
```kotlin
fun generatePrintFunction(stringToPrint) {
    function = fun() {
        // The stringToPrint variable is captured here
        // and saved for later in this function
        print(stringToPrint);
    };
    return function;
}

printFunction = generatePrintFunction("FOO");
// printFunction now contains a lambda that prints a string
// the lambda has the variable "FOO" saved in it that it will use
// when we call the printFunction

printFunction(); // prints "FOO"
```

Another example where the variable changed between function calls:
```kotlin
variable = 10;

fun printSomething() {
	print(variable);
}

printSomething(); // prints 10

variable = 20;
printSomething(); // prints 20
```

### Overloading Functions

Overloading functions is the ability to have functions that have a different number of parameters defined separately and not interfere with each other, overloading functions are possible inside of classes which we will cover later, but defining functions in the scope you cannot overload them, one will simply overwrite the other. This may be possible at a later date, at which point this section of the documentation will be updated.

Here is an example:
```kotlin
fun exampleFunction() {
    print("ExampleFunction");
}

exampleFunction(); // prints "ExampleFunction"

// Define another function with name exampleFunction
// with different amount of parameters
fun exampleFunction(parameter) {
    print("ExampleFunctionWithParam");
}

exampleFunction(null); // prints "ExampleFunctionWithParam"

exampleFunction(); // throws an error since original function was overwritten
```  

This however is not the case with built-in functions, since they are implemented internally they work slightly different and as such they can be overloaded, all information on built in overloads will be documented separately, see the next section on Built-In Functions.

### Delegating Functions

Delegating a function is when instead of calling the function you use it as if it were an object and store the action function as a value much like a lambda:
```kotlin
fun delegateExample() {
	print("called");
}

del = delegateExample; // We didn't call the function
// Since 'del' now has a function stored in it we can call it
del(); // prints 'called'
```

Previously, before Arucas 2.0.0 you could not delegate a function that was overloaded however in Arucas 2.0.0+ we can now do that too.
```kotlin
fun overload() {
	print("0");
}

fun overload(arg) {
	print("1");
}

fun overload(arg1, arg2) {
	print("2");
}

delegate = overload;
// Delegate does not store the 'overload' function but
// instead stores the information to be able to call
// the 'overload' function at a later point

// This means we can do this:
delegate(); // prints '0'
delegate(0); // prints '1'
delegate(0, 0); // prints '2'

// However if you give incorrect number of args:
delegate(0, 0, 0); // Error
```

### Built-In Functions

Throughout this documentation I have been using built-in functions, these functions are implemented natively in the language that allow you to do the basic functions, some examples that I have been using are `print`, `input`, and `sleep`.
There is a list of these basic functions and these provide some key functionality to the language, so you should review them, and they can be found on the separate page [here](https://github.com/senseiwells/Arucas/blob/main/docs/Extensions.md). 

## Members

Members are part of values, they can either be functions or fields. They allow you to interact and use part of the values. These are usually accessed through the `.` operator, followed by the field or function name (and brackets if you are calling the function). Classes can also have members, these are known as static members, static members are not based on an object but instead on the class definition itself.
```kotlin
hello = "hElLo";
// Lowercase is a method of <String>, and
// when it gets called it returns a complete
// lowercase representation of the string
print(hello.lowercase());

// type is a static field of the String class
// this is the type that represents the class
print(String.type);
```

Usually each class has its own static members and each value has its own members too, these are documented on a separate page, and you can find that [here](https://github.com/senseiwells/Arucas/blob/main/docs/Classes.md).

## Lists

Lists are a form of collection, they are the key data structure in the language, they allow you to store multiple values inside of one value, lists in Arucas are dynamic meaning that they do not have a fixed size you are also able to put any type of value in a list. Lists are an ordered data structure, meaning their order stays consistent with how you input values into the list.

### Simple Lists

Lists are very simple to use, as mentioned earlier in the documentation they can be declared with the List literal `[]`:
```kotlin
// Creating an empty list
list = [];
```

You are also able to put values inside the square brackets to declare a list with items in them.
```kotlin
// List with values 1, 2, "string"
list = [1, 2, "string"];
```

We can get the number of values inside a list by using a built-in function: `len`:
```kotlin
print(len([true, false, null])); // -> 3

print(len([])); // -> 0
```

### Using Lists

An important concept is understanding indexes of lists, each value as an index in a list with the first value in the list having an index of 0 and then incrementing by one until the last value. We can then use this to access values in the list.
```kotlin
list = ["first", "second", "third"];

// Index 0 corralates to the first index
print(list.get(0)); // -> "first"

// A short hand for accessing lists was introduced in 1.2.0
// We can use the [] operator to access an index in the list
print(list[1]); // -> "second"
```
Something to note is that if an index is provided that is outside the bounds of the list, then an error will be thrown.

To manipulate the contents of the list, we can take a look at the available methods:
```kotlin
list = [1, 2, 3];

// append method adds a value **to the end** of the list
list.append(4);

// insert method adds a value at a specific index in the list
list.insert(0, 0); // list = [0, 1, 2, 3]

// remove methods removes a value at a specific index
list.remove(3); // list = [0, 1, 2]

// set method sets the value at a specific index
list.set("zero", 0); // list = ["zero", 1, 2]

// A short hand for assigning indexes of lists was introduced in 1.2.0
// We can again use the [] operator to assign an index in the list
list[1] = "one"; // list = ["zero", "one", 2]
```

### List Unpacking

Lists provide special functionality in Arucas as they provide the ability to unpack them. This means you are able to extract all the variables in the list into variables. Here is an example:
```kotlin
// position with list having x, y, and z coordinates
position = [100, 50, -900];

// If we wanted to extract those we could do this:
x = position.get(0);
y = position.get(1);
z = position.get(2);

// Or we can use a shorthand: list unpacking
x, y, z = position; // x = 100, y = 50, z = -900

// To be able to do this the number of variables
// you are assigning must be equal to the length
// of the list, otherwise it will throw an error
```

Changing the values in the variables will not change the values in the list:
```kotlin
position = [100, 50, 200];
print(position); // -> [100, 50, 200]

x, y, z = position; // x = 100, y = 50, z = 200

x = 10;
// We now set x to 10 but this does not change
// the value in the list that x was assigned first

print(position); // -> [100, 50, 200]
```

## Maps

Maps like lists are a form of collection, maps can be seen as similar to lists, but instead of using indexes to access a value maps allow you to define a specific key value to access certain values. Maps have a literal, and by default these will be ordered based on the order they are inputted. Maps also have no fixed size.

### Simple Maps

Maps have a literal: `{}` which can be used to create maps. In the literal, you must declare a key and a value which are separated by a colon:
```kotlin
map = {"key": "value"};

// We can also declare an empty map
map = {};
```
Any types can be used for keys or values, for custom classes you need to ensure they have an appropriate hashing function, however we will cover this later in the classes section.

To define multiple key value pairs we just separate them with a comma, this is usually done over multiple lines:
```kotlin
map = {
    "key": "value",
    "otherKey": "value",
    "foo": "bar"
};
```

Similarly to Lists we can get the length of the map by using `len`, this returns the number of key value pairs:
```kotlin
len({"a": "A", "b": "B"}); // -> 2

len({}); // -> 0
```

### Using Maps

Maps are very similar to lists except using specific keys to access and assign values, here are some examples:
```kotlin
map = {
    "one": 1,
    "two": 2,
    "three": 3
};

// get method allows us to get a value using a key
print(map.get("one")); // -> 1
// Similarly to lists maps also allow the short hand by

// using the bracket operator
print(map["two"]); // -> 2

// put method allows us to add a key value pair
// this will replace any previous value with the given key
map.put("four", 4); // map = {"one": 1, "two": 2, "three": 3, "four": 4}

// Here is an example of it replacing an existing key
map.put("four", 0); // map = {"one": 1, "two": 2, "three": 3, "four": 0}

// Similar to list we can also set keys by using the bracket operator
map["four"] = 4; // map = {"one": 1, "two": 2, "three": 3, "four": 4}

// remove method removes a key and value from the map
map.remove("one"); // map = {"two": 2, "three": 3, "four": 4}
```

Some other useful methods of the map are those that get all the keys and values:
```kotlin
map = {
    "one": 1,
    "two": 2,
    "three": 3
};

// These methods return lists containing keys and values
keys = map.getKeys(); // keys = ["one", "two", "three"]
values = map.getValues(); // values = [1, 2, 3]
```

Another useful thing to note is that when using the `foreach` loop maps will loop over their keys, inside the loop you can the use it to access the value:
```kotlin
map = {
    "one": 1,
    "two": 2,
    "three": 3
};

foreach (key : map) {
    value = map[key];
    // Do something
}
```

### Sets

Sets are another form of collection. A set is basically just a map that doesn't have values, this allows for a list-like collection, however you cannot access the values using an index and cannot have duplicate values in the set. Sets have the benefit that they are faster to search than lists, an example of this will be shown in the section.

Unlike Maps and Lists, Sets do not have a literal form and so you will need to use the `Set` class to create a set, we do this by using the `of` method that can take an arbitrary amount of parameters:
```kotlin
// Empty set
Set.of();

Set.of(1, 2, 3); // -> <1, 2, 3>
```

As previously mentioned sets have the benefit of a fast searching algorithm, here is an example:
```kotlin
validNames = Set.of("Foo", "Bar", "Baz");

name = "Some Example Name";

// contains method is much faster than list
if (validNames.contains(name)) {
    print("You are valid");
}
```

## Errors

### Creation

### Throwing 

### Catching

## Imports

## Classes

Classes in Arucas allow for abstraction, they provide a way to encapsulate values into another value, and classes let you define certain behaviour with the value such as interactions with operators and the methods that the value has.

### Syntax

The class syntax is very simple and similar to many other languages, we use the `class` keyword to declare a class definition followed by the name of the class and then a series of class statements which we will cover further on in this section.
```kotlin
// Classes should follow Pascal Naming
class ExampleClass {
}
```

### Constructors

Constructors are essentially functions that are run when the class is instantiated. These are often used to set fields and often take parameters. By default, if no constructor is declared then a synthetic constructor is created: you will be able to construct the class without any parameters. However, if any constructors are defined this synthetic constructor will not be available.

To define a constructor in a class, we use the class name followed by brackets which can contain parameters and then followed by a statement which is run when the class is instantiated.
```kotlin
class ExampleClass {
    // Constructor
    ExampleClass() {
    }
}
```

Since Arucas 2.0.0 you can also call an overloaded constructor inside another constructor. This can be done by calling `this` after your constructor definition:
```kotlin
class ExampleClass {
	ExampleClass(number) {
	}
	// Here we call this() constructor
	// with a parameter of 10
	ExampleClass(): this(10) {
	}
}
```

When you do this the referenced constructor will always be executed first.

### Fields

Fields are essentially variables that are stored in a class and can be accessed by that class at anytime. As of writing this documentation all fields are public and mutable meaning that anyone can access and modify the value of a field.

Fields are defined in the class body using the `var` keyword and can also optionally be type hinted to enforce only specific types to be allowed in the field.
```kotlin
class Example {
	var exampleField;
	var typedField: String;
}
```

An issue with the current version is that typed fields will always be null until they are assigned a value, this essentially means that you you cannot have a typed field without it accepting `Null` or having it assigned when created. Which brings me onto the next point, fields can be initialised in the body, the expression will be re-evaluated everytime a class is created:
```kotlin
class Example {
	var exampleField = "initialised";
}
```

Fields are what allows for encapsulation since you can have as many fields as you want in a class:
```kotlin
class Person {
	var name;
	var age;
	var height;
	var gender;
	// ...
}
```

### Methods

Methods are just functions that belong to a class. Methods are defined like functions but instead they are declated inside the class body.
```kotlin
class Example {
	fun sayHello() {
		print("hello!");
	}
}
```

To use a method you need an instance of the class to call it.
```kotlin
e = new Example(); // Instance of the Example class
e.sayHello(); // prints 'hello!'
```

Methods are special in the fact that they implicitly pass the calling object into the method allowing you to access fields and other methods from within that method, you can reference this calling object with the keyword `this`.
```kotlin
class Example {
	var string;
	Example(string) {
		// this references this object
		// you are setting the field 'string'
		this.string = string;
	}
	fun say() {
		// We access the 'string'
		// field on this
		print(this.string);
	}
}
```

This may seem confusing, what does `this` mean? Well internally it works the same as a regular function, `this` is just a parameter that is passed in implicitly:
```kotlin
// this is a keyword thus this example
// would fail to compile, but just an example
fun say(this) {
	print(this.string);
}
class Example {
	var string;
	Example(string) {
		this.string = string;
	}
}
e = new Example("foo");
say(e); // prints 'foo'
```

Other than that methods work exactly the same as functions which you can read about [here](https://github.com/senseiwells/Arucas/blob/main/docs/Language%20Documentation/14.%20Functions.md).

### Operators

Arucas allows you to declare how operations should work between classes. You defined operations much like methods but instead of using `fun` you use the `operator` keyword and instead of being followed by a name you follow it with the operator you want to override:
```kotlin
class Example {
	var number = 10;
	operator + (other: Example) {
		return this.number + other.number;
	}
}
e1 = new Example();
e1.number = 22;
e2 = new Example();
print(e1 + e2); // prints 32
```

The parameters in the operator are also significant since you can override both unary and binary operators (and technically a ternary).

Here is a table of all the overridable operators:

#### Unary (no parameters):
Name | Operator | Example
-|-|-
NOT | `!` | `!false`
PLUS | `+` | `+10`
MINUS | `-` | `-10`

#### Binary (one parameter):
Name | Operator | Example
-|-|-
PLUS | `+` | `1 + 1`
MINUS | `-` | `1 - 1`
MULTIPLY | `*` | `2 * 2`
DIVIDE | `/` | `4 / 2`
POWER | `^` | `2 ^ 2`
LESS_THAN | `<` | `55 < 90`
LESS_THAN_EQUAL | `<=` | `43 <= 10`
MORE_THAN | `>` | `66 > 22`
MORE_THAN_EQUAL | `>=` | `78 >= 0`
EQUAL | `==` | `6 == 6`
NOT_EQUAL | `!=` | `"wow" != "foo"`
AND | `&&` | `true && false`
OR | `\|\|` | `false \|\| true`
XOR | `~` | `true ~ false`
SHIFT_LEFT | `<<` | `2 << 1`
SHIFT_RIGHT | `>>` | `2 >> 1`
BIT_AND | `&` | `56 & 7`
BIT_OR | `\|` | `92 \| 45`
SQUARE_BRACKET | `[]` | `[8, 9, 10][2]`

#### Ternary (two parameters)
Name | Operator | Example
-|-|-
SQUARE_BRACKET | `[]` | `[8, 9, 10][2] = 11`

### Static Methods and Fields

Static methods and fields work very much like the global scope, you can define variables and functions in a class that do not need an instance of the class to be called. The reason you may want to put your methods in a class as static instead of the global scope is because it stops cluttering the global scope and more importantly allows other script to be able to use your function; as the global scope cannot be imported, only classes can thus static methods can be used whereas global functions cannot.

Defining a static field or method is extremely easy, it is the same as a regular method or field but instead has the `static` keywork before it.
```kotlin
class Example {
	static fun staticMethod() {
		print("Called static method");
	}
}
```

And to call a static method you just use the class name followed by the dot operator then the method name and any arguments:
```kotlin
Example.staticMethod(); // prints 'Called static method'
```

Static methods do not have access to `this` because there is no class instance to access.

### Inheritance

Inheritance was introduced in Arucas 2.0.0. It allows for your classes to inherit methods and fields from a parent class. Arucas does not support multi-inheritance however does support interface inheritance.

To inherit a class you simply add a colon after your class name and then follow that with the name of the class you wish to inherit from.
```kotlin
class Parent {
}
class Child: Parent {
}
```

When inheriting from a parent class (or superclass) you must initialise the parents constructor, this is enforced to avoid unexpected behaviours, this is required even if the parent has a default constructor or a constructor with no parameters. You can do this by adding a colon after your constructor and calling `super`.

```kotlin
class Parent {
	Parent() {
		print("Constructing parent!");
	}
}
class BadChild: Parent {
	// This will throw an error because
	// the child class is not initialising
	// the parents constructor
	BadChild() {
	}
}
// Assuming it were to compile
new BadChild(); // this would print nothing
class GoodChild: Parent {
	GoodChild(): super() {
	}
}
new GoodChild(); // This will print 'Constructing parent!'
```

Another thing to note is that the child class does not need to directly call the super constructor, as long as it is called at some point it is allowed. For example you can call an overloaded constructor that eventually calls the super constructor:
```kotlin
class Parent {
	Parent() {
		print("Constructing parent!");
	}
}
class Child: Parent {
	// This calls super
	Child(): super() {
	}
	// This class constructor with no args
	Child(argument): this() {
	}
}
new Child(); // prints 'Constructing parent!'
```

As mentioned child classes inherit methods and fields from their parent classes:
```kotlin
class Parent {
	var foo;
	Parent(bar) {
		this.foo = bar;
	}
	fun printFoo() {
		print(this.foo);
	}
}
class Child: Parent {
	Child(bar): super(bar);
	fun getFoo() {
		return this.foo;
	}
}
c = new Child("foo");
c.getFoo(); // -> 'foo'
c.printFoo(); // prints 'foo'
c.foo; // -> 'foo'
```

As well as inheriting child classes can override methods.
```kotlin
class Parent {
	var foo;
	Parent(bar) {
		this.foo = bar;
	}
	fun something() {
		print("Parent something!");
	}
	fun callSomething() {
		this.something();
	}
}
class Child: Parent {
	Child(): super("foo!");
	fun something() {
		print("Child something!");
	}
}
c = new Child();
p = new Parent("bar");
c.callSomething(); // prints 'Child something!'
p.callSomething(); // prints 'Parent something!'
```

Even if a child overrides a parents method or operator it can still access it by using the `super` keyword, this just calls the method belonging to the parent.
```kotlin
class Parent {
	operator + (other) {
		return -1;
	}
	fun getSomething() {
		return "Parent";
	}
}
class Child: Parent {
	Child(): super();
	operator + (other) {
		if (other == 3) {
			return this.getSomething();
		}
		if (other == 4) {
			return super.getSomething();
		}
		return super + other;
	}
	fun getSomething() {
		return "Child";
	}
}
c = new Child();
c + 3; // -> "Child"
c + 4; // -> "Parent"
c + 0; // -> -1
```

An interesting thing to also note is how types work with inheritance. using the static method `Type.of(<Object>)` gets the exact type of the object:
```kotlin
class Parent {
	fun getTypeName() {
		return Type.of(this).getName();
	}
}
class Child: Parent {
	Child(): super();
}
new Parent().getTypeName(); // "Parent"
new Child().getTypeName(); // "Child"
```

This is because the `this` reference inside of the `Parent` class is of a type that is or a child class of `Parent` and since `Type.of(<Object>)` returns the exact type it will always be that of `Parent` or a child class of itself.

It is also worthy to note that you are able to extend some built-in classes if they allow it. For example you are permitted to extend the `Function` class:
```kotlin
class Example: Function {
	Example(): super();
	// This is the method that gets called
	// when you call a function with '()'
	fun invoke() {
		print("hi");
	}
	// You can also define
	// invoke with multiple parameters
	fun invoke(arg1, arg2) {
		print("hi two!");
	}
}
// So you can do stuff like this:
e = new Example();
e(); // Regular 'invoke' with no arguments, prints 'hi'
e(0, 0); // 'invoke' with 2 arguments, prints 'hi two!'
```

### Interfaces

While interfaces are still technically inheritance I split it up since the last section is quite big. What interfaces allow you to do is make a blueprint for a class, and if a class decides to implement an interface it **must** implement all the functions that were specified in an interface.

A class can implement multiple interfaces and if the methods are not implemented an error will be thrown. This is useful to be able to ensure that the values you pass around have specific methods. The requirements for a method to be implemented is for it to have the same name and same number of parameters. Like regular overriding of methods this does not force you to inherit the types however it is good practice to do so.
```kotlin
interface Addable {
	fun add(other);
}
class NoAdd {
}
class Add: Addable {
	fun add(other) {
		return 10;
	}
}
// We specifically tell this function that
// the first parameter must be of the type
// Addable, this ensures that we have a method
// 'add' that we can call.
fun addAny(first: Addable, second) {
	first.add(second);
}
addAny(new Add(), "foobar"); // -> 10
addAny(new NoAdd(), "foobar"); // Error
```

Interfaces can work alongside class inheritance and as mentioned you can implement as many interfaces as needed. Interfaces are also permitted to be implemented on enums.
```kotlin
// Kinda pointless to have in interface
// with nothing in it, but this is an example
interface A { }
interface B { }
class Parent { }
// This doesn't need to be in a specific order
class Child: Parent, A, B {
	Child(): super();
}
```

## Enums

### Syntax

### Constructors

## Threads

### Purpose

### Creation

### Stopping Threads

### Thread Safety

## Java Integration

If there are specific things you want to achieve that aren't possible with the base language, you may want to look into calling Java code from within your script. This is possibly by using the `util.Internal` library and importing the `Java` class.
```kotlin
import Java from util.Internal;
``` 

### Java Types

There are many static methods for the Java class, and these will be key for creating Java typed values. One such method is `valueOf`, this converts any Arucas typed value into a Java one:
```kotlin
import Java from util.Internal;

jString = Java.valueOf(""); // Arucas String type -> Java String type
```

All Java typed values have the Arucas type of `Java` and they all have some basic methods you can use, these allow you to access their methods and fields which we will explore later in this documentation. Another method which is important is the `toArucas` method, which tries to convert the Java typed value back into an Arucas typed value.
```kotlin
import Java from util.Internal;

jString = Java.valueOf(""); // Java String type

string = jString.toArucas(); // Back to Arucas String type
``` 

Not every Java type has a conversion and so if you try to convert a Java type that does not have a conversion it will simply just return itself.

### Methods and Fields

`Java` values have a property that allows them to call Java methods, there are different ways this can be done, but the advised way is to call the method as usual:
```kotlin
import Java from util.Internal;

jString = Java.valueOf("");
// Java methods return Java typed values too
// The isBlank method is a Java method!
jBoolean = jString.isBlank();

if (jBoolean.toArucas()) {
    print("String was blank");
}
```
You are also able to call methods with parameters the same way you would call an Arucas function, however the types of the values must match the method signature, the arguments you pass in should generally be Java typed.

Something to note about methods is that they use the Java reflection library internally, which makes calling Java methods quite slow. On a small scale this is fine, however if you plan to repeatedly call a method you should consider delegating the method. When the method is delegated, the Internal library creates a `MethodHandle` which is significantly faster.
```kotlin
import Java from util.Internal;

jString = Java.valueOf("");
delegate = jString.isBlank;

for (i = 0; i < 100; i++) {
    delegate();
}
```

Accessing fields is also similar to Arucas this can be done by just using the dot operator:
```kotlin
import Java from util.Internal;

array = Java.arrayOf();
// length field of Java array type
array.length;
```

### Constructing Java Objects

Now this is great, but what if we want to construct a Java Object? Well we can use `Java.constructClass()`, this method takes in the class name and then any amount of parameters:
```kotlin
import Java from util.Internal;

ArrayList = "java.util.ArrayList";

// From looking at Java code this would invoke the
// constructor with no parameters
jList = Java.constructClass(ArrayList);

// Adding Java Strings into ArrayList
jList.add("One"); 
jList.add("Two");
```

As mentioned before Arucas values can be converted to Java values, and you have the ability to construct Java classes, but there are still some cases where Java type values cannot be created. These are primitives, arrays, and lambdas. To remedy this, the Java class provides static methods to create these types of values:
```kotlin
import Java from util.Internal;

Java.intOf(10); // Creates Java int type
Java.floatOf(9.5); // Creates Java float type
Java.charOf("h"); // Creates Java char type
// ...

Java.arrayOf("wow", 7, false); // Creats Object[] with values, arbitrary arguments
Java.intArray(10); // Creats int[] with size passed in
Java.byteArray(10); // Creates byte[] with size passed in
// ...

// Runnables take no args and returns nothing
Java.runnableOf(fun() {
    print("runnable!");
});
// Consumables take 1 arg and returns nothing
Java.consumerOf(fun(arg) {
    print("consumer!: " + arg);
});
// Suppliers take no args and returns something
Java.supplierOf(fun() {
    print("supplier!");
    return false;
});
// Functions take 1 arg and returns something
Java.functionOf(fun(arg) {
    print("function!: " + arg);
    return true;
});
```

### Static Methods and Fields

Now we know how we can construct objects and call their methods in Java, what about static methods and fields? Well, this is done again through the Java class with a static method:
```kotlin
import Java from util.Internal;

Integer = "java.lang.Integer";

// Class name, method name, parameters...
Java.callStaticMethod(Integer, "parseInt", "120");

// Class name, field name
Java.getStaticField(Integer, "MAX_VALUE");

// Class name, field name, new value (must be correct type)
// Obviously this won't work, but it's just an example
Java.setStaticField(Integer, "MAX_VALUE", Java.intOf(100));"
```



## BuiltInExtension

### `debug(bool)`
- Description: This is used to enable or disable debug mode
- Parameter - Boolean (`bool`): true to enable debug mode, false to disable debug mode
- Example:
```kotlin
debug(true);
```

### `eval(code)`
- Description: This is used to evaluate a string as code.
This will not inherit imports that are in the parent script
- Parameter - String (`code`): the code to evaluate
- Returns - Object: the result of the evaluation
- Example:
```kotlin
eval('1 + 1');
```

### `experimental(bool)`
- Description: This is used to enable or disable experimental mode
- Parameter - Boolean (`bool`): true to enable experimental mode, false to disable experimental mode
- Example:
```kotlin
experimental(true);
```

### `getArucasVersion()`
- Description: This is used to get the version of Arucas that is currently running
- Returns - String: the version of Arucas that is currently running
- Example:
```kotlin
getArucasVersion();
```

### `getDate()`
- Description: This is used to get the current date formatted with dd/MM/yyyy in your local time
- Returns - String: the current date formatted with dd/MM/yyyy
- Example:
```kotlin
getDate();
```

### `getMilliTime()`
- Description: This is used to get the current time in milliseconds
- Returns - Number: the current time in milliseconds
- Example:
```kotlin
getMilliTime();
```

### `getNanoTime()`
- Description: This is used to get the current time in nanoseconds
- Returns - Number: the current time in nanoseconds
- Example:
```kotlin
getNanoTime();
```

### `getTime()`
- Description: This is used to get the current time formatted with HH:mm:ss in your local time
- Returns - String: the current time formatted with HH:mm:ss
- Example:
```kotlin
getTime();
```

### `getUnixTime()`
- Description: This is used to get the current time in seconds since the Unix epoch
- Returns - Number: the current time in seconds since the Unix epoch
- Example:
```kotlin
getUnixTime();
```

### `input(prompt)`
- Description: This is used to take an input from the user
- Parameter - String (`prompt`): the prompt to show the user
- Returns - String: the input from the user
- Example:
```kotlin
input('What is your name?');
```

### `isMain()`
- Description: This is used to check whether the script is the main script
- Returns - Boolean: true if the script is the main script, false if it is not
- Example:
```kotlin
isMain();
```

### `len(sizable)`
- Description: This is used to get the length of a collection or string
- Parameter - String (`sizable`): the collection or string
- Example:
```kotlin
len("Hello World");
```

### `print(printValue)`
- Description: This prints a value to the output handler
- Parameter - Object (`printValue`): the value to print
- Example:
```kotlin
print('Hello World');
```

### `print(printValue...)`
- Description: This prints a number of values to the console
If there are no arguments then this will print a new line,
other wise it will print the contents without a new line
- Parameter - Object (`printValue...`): the value to print
- Example:
```kotlin
print('Hello World', 'This is a test', 123);
```

### `printDebug(printValue)`
- Description: This logs something to the debug output.
It only prints if debug mode is enabled: `debug(true)`
- Parameter - Object (`printValue`): the value to print
- Example:
```kotlin
debug(true); // Enable debug for testing
if (true) {
    printDebug("Inside if statement");
}
```

### `random(bound)`
- Description: This is used to generate a random integer between 0 and the bound
- Parameter - Number (`bound`): the maximum bound (exclusive)
- Returns - Number: the random integer
- Example:
```kotlin
random(10);
```

### `range(bound)`
- Description: This is used to generate a range of integers starting from 0, incrementing by 1
- Parameter - Number (`bound`): the maximum bound (exclusive)
- Returns - Iterable: an iterable object that returns the range of integers
- Example:
```kotlin
range(10);
```

### `range(start, bound)`
- Description: This is used to generate a range of numbers starting
from a start value and ending at a bound value incrementing by 1
- Parameters:
  - Number (`start`): the start value
  - Number (`bound`): the maximum bound (exclusive)
- Returns - Iterable: an iterable object that returns the range of integers
- Example:
```kotlin
range(0, 10);
```

### `range(start, bound, step)`
- Description: This is used to generate a range of numbers starting from a
start value and ending at a bound value incrementing by a step value
- Parameters:
  - Number (`start`): the start value
  - Number (`bound`): the maximum bound (exclusive)
  - Number (`step`): the step value
- Returns - Iterable: an iterable object that returns the range of integers
- Example:
```kotlin
range(0, 10, 2);
```

### `run(path)`
- Description: This is used to run a .arucas file, you can use on script to run other scripts
- Parameter - String (`path`): as a file path
- Returns - Object: any value that the file returns
- Example:
```kotlin
run('/home/user/script.arucas');
```

### `runFromString(code)`
- Deprecated: This should be replaced with 'eval(code)'
- Description: This is used to evaluate a string as code.
This will not inherit imports that are in the parent script
- Parameter - String (`code`): the code to run
- Example:
```kotlin
runFromString('print("Hello World");');
```

### `sleep(milliseconds)`
- Description: This pauses your program for a certain amount of milliseconds
- Parameter - Number (`milliseconds`): milliseconds to sleep
- Example:
```kotlin
sleep(1000);
```

### `stop()`
- Description: This is used to stop a script
- Example:
```kotlin
stop();
```

### `suppressDeprecated(bool)`
- Description: This is used to enable or disable suppressing deprecation warnings
- Parameter - Boolean (`bool`): true to enable, false to disable warnings
- Example:
```kotlin
suppressDeprecated(true);
```


## MinecraftExtension

### `hold()`
- Description: This freezes the current thread and halts execution, same functionality as 'Thread.freeze()'
- Example:
```kotlin
hold();
```


# Biome class
Biome class for Arucas

This class represents biomes, and allows you to interact with things inside of them.
Import with `import Biome from Minecraft;`

## Methods

### `<Biome>.canSnow(pos)`
- Description: This function calculates wheter snow will fall at given coordinates
- Parameter - Pos (`pos`): the position
- Returns - Boolean: whether snow will fall at given position
- Example:
```kotlin
biome.canSnow(new Pos(0, 100, 0));
```

### `<Biome>.canSnow(x, y, z)`
- Description: This function calculates wheter snow will fall at given coordinates
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Boolean: whether snow will fall at given position
- Example:
```kotlin
biome.canSnow(0, 100, 0);
```

### `<Biome>.getFogColor()`
- Description: This function returns Fog color of the biome
- Returns - Number: fog color of the biome
- Example:
```kotlin
biome.getFogColor();
```

### `<Biome>.getId()`
- Description: This function returns the path id of the biome, e.g. 'plains'
- Returns - String: id of the biome
- Example:
```kotlin
biome.getId();
```

### `<Biome>.getSkyColor()`
- Description: This function returns sky color of the biome
- Returns - Number: sky color of the biome
- Example:
```kotlin
biome.getSkyColor();
```

### `<Biome>.getTemperature()`
- Description: This function returns temperature of the biome
- Returns - Number: temperature of the biome
- Example:
```kotlin
biome.getTemperature();
```

### `<Biome>.getWaterColor()`
- Description: This function returns Fog color of the biome
- Returns - Number: fog color of the biome
- Example:
```kotlin
biome.getWaterColor();
```

### `<Biome>.getWaterFogColor()`
- Description: This function returns water fog color of the biome
- Returns - Number: water fog color of the biome
- Example:
```kotlin
biome.getWaterFogColor();
```

### `<Biome>.hasHighHumidity()`
- Description: This function returns if biome has high humidity
- Returns - Boolean: whether biome has high humidity
- Example:
```kotlin
biome.hasHighHumidity();
```

### `<Biome>.isCold(pos)`
- Description: This function calculates wheter biome is cold at given position
- Parameter - Pos (`pos`): the position
- Returns - Boolean: whether temperature is cold at given position
- Example:
```kotlin
biome.isCold(0, 100, 0);
```

### `<Biome>.isCold(x, y, z)`
- Description: This function calculates wheter biome is cold at given position
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Boolean: whether temperature is cold at given position
- Example:
```kotlin
biome.isCold(0, 100, 0);
```

### `<Biome>.isHot(pos)`
- Description: This function calculates wheter biome is hot at given position
- Parameter - Pos (`pos`): the position
- Returns - Boolean: whether temperature is hot at given position
- Example:
```kotlin
biome.isHot(0, 100, 0);
```

### `<Biome>.isHot(x, y, z)`
- Description: This function calculates wheter biome is hot at given position
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Boolean: whether temperature is hot at given position
- Example:
```kotlin
biome.isHot(0, 100, 0);
```



# Block class
Block class for Arucas

This class allows interactions with blocks in Minecraft.
Import with `import Block from Minecraft;`

## Methods

### `<Block>.getBlastResistance()`
- Description: This gets the blast resistance of the Block
- Returns - Number: the blast resistance of the Block
- Example:
```kotlin
block.getBlastResistance();
```

### `<Block>.getBlockNbt()`
- Description: This gets the NBT of the Block
- Returns - Map: the NBT of the Block, may be null if the Block has no NBT
- Example:
```kotlin
block.getBlockNbt();
```

### `<Block>.getBlockProperties()`
- Description: This gets the properties of the Block
You can find a list of all block properties
[here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Block_states)
- Returns - Map: the properties of the Block, may be empty if there are no properties
- Example:
```kotlin
block.getBlockProperties();
```

### `<Block>.getDefaultState()`
- Description: This gets the default state of the block, it will conserve any positions
- Returns - Block: default state of the Block
- Example:
```kotlin
block.getDefaultState();
```

### `<Block>.getHardness()`
- Description: This gets the hardness of the Block
- Returns - Number: the hardness of the Block
- Example:
```kotlin
block.getHardness();
```

### `<Block>.getLuminance()`
- Description: This gets the luminance of the Block
- Returns - Number: the luminance of the Block
- Example:
```kotlin
block.getLuminance();
```

### `<Block>.getMapColour()`
- Description: This gets the map colour of the Block, can also be called with 'getMapColor'
- Returns - List: a list with the map colour of the Block as RGB values
- Example:
```kotlin
block.getMapColour();
```

### `<Block>.getMaterial()`
- Description: This gets the material of the Block
- Returns - Material: the material of the Block
- Example:
```kotlin
block.getMaterial();
```

### `<Block>.getPos()`
- Description: This gets the position of the Block
- Returns - Pos: the position of the Block, may be null if the Block has no position
- Example:
```kotlin
block.getPos();
```

### `<Block>.getX()`
- Description: This gets the X position of the Block
- Returns - Number: the X position of the Block, may be null if the Block has no position
- Example:
```kotlin
block.getX();
```

### `<Block>.getY()`
- Description: This gets the Y position of the Block
- Returns - Number: the Y position of the Block, may be null if the Block has no position
- Example:
```kotlin
block.getY();
```

### `<Block>.getZ()`
- Description: This gets the Z position of the Block
- Returns - Number: the Z position of the Block, may be null if the Block has no position
- Example:
```kotlin
block.getZ();
```

### `<Block>.hasBlockPosition()`
- Description: This checks if the Block has a position or not
- Returns - Boolean: true if the Block has a position
- Example:
```kotlin
block.hasBlockPosition();
```

### `<Block>.isBlockEntity()`
- Description: This checks if the Block is a BlockEntity
- Returns - Boolean: true if the Block is a BlockEntity
- Example:
```kotlin
block.isBlockEntity();
```

### `<Block>.isFluid()`
- Description: This checks if the Block is a fluid
- Returns - Boolean: true if the Block is a fluid
- Example:
```kotlin
block.isFluid();
```

### `<Block>.isFluidSource()`
- Description: This checks if the Block is a fluid source
- Returns - Boolean: true if the Block is a fluid source
- Example:
```kotlin
block.isFluidSource();
```

### `<Block>.isReplaceable()`
- Description: This checks if the Block is replaceable
- Returns - Boolean: true if the Block is replaceable
- Example:
```kotlin
block.isReplaceable();
```

### `<Block>.isSideSolidFullSquare(side)`
- Description: This checks if the Block is solid on the full square
- Parameter - String (`side`): the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'
- Returns - Boolean: true if the Block is solid on the full square
- Example:
```kotlin
block.isSideSolidFullSquare('north');
```

### `<Block>.isSolidBlock()`
- Description: This checks if the Block is a solid block
- Returns - Boolean: true if the Block is a solid block
- Example:
```kotlin
block.isSolidBlock();
```

### `<Block>.isSpawnable()`
- Description: This checks if the Block is spawnable in the case of zombies
- Returns - Boolean: true if the Block is spawnable in the case of zombies
- Example:
```kotlin
block.isSpawnable();
```

### `<Block>.isSpawnable(entity)`
- Description: This checks if the Block allows spawning for given entity
- Parameter - Entity (`entity`): the entity to check
- Returns - Boolean: true if the Block allows spawning for given entity
- Example:
```kotlin
block.isSpawnable(zombie);
```

### `<Block>.isTransparent()`
- Description: This checks if the Block is transparent
- Returns - Boolean: true if the Block is transparent
- Example:
```kotlin
block.isTransparent();
```

### `<Block>.mirrorFrontBack()`
- Description: This mirrors the Block around the front and back
- Returns - Block: the mirrored Block
- Example:
```kotlin
block.mirrorFrontBack();
```

### `<Block>.mirrorLeftRight()`
- Description: This mirrors the Block around the left and right
- Returns - Block: the mirrored Block
- Example:
```kotlin
block.mirrorLeftRight();
```

### `<Block>.rotateYClockwise()`
- Description: This rotates the Block 90 degrees clockwise
- Returns - Block: the rotated Block
- Example:
```kotlin
block.rotateYClockwise();
```

### `<Block>.rotateYCounterClockwise()`
- Description: This rotates the Block 90 degrees counter-clockwise
- Returns - Block: the rotated Block
- Example:
```kotlin
block.rotateYCounterClockwise();
```

### `<Block>.sideCoversSmallSquare(side)`
- Description: This checks if the Block covers a small square
- Parameter - String (`side`): the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'
- Returns - Boolean: true if the Block covers a small square
- Example:
```kotlin
block.sideCoversSmallSquare('north');
```

### `<Block>.with(property, value)`
- Description: This gets modified block with a property value, conserving positions
- Parameters:
  - String (`property`): property name, such as 'facing', 'extended'
  - String (`value`): value name, such as 'north', 'true'
- Returns - Block: new state of the Block
- Example:
```kotlin
block.with('facing', 'north');
```

## Static Methods

### `Block.of(material)`
- Description: This creates a Block from a material or string
- Parameter - Material (`material`): the material, item stack, block, or string to create the Block from
- Returns - Block: the Block created from the material or string
- Example:
```kotlin
Block.of(Material.STONE);
```



# Boolean class
Boolean class for Arucas

This is the boolean type, representing either true or false.
This class cannot be instantiated, or extended
Class does not need to be imported



# BoxShape class
BoxShape class for Arucas

This class allows you to create box shapes that can be rendered in the world.
Import with `import BoxShape from Minecraft;`

## Constructors

### `new BoxShape(pos)`
- Description: Creates a new box shape, this is used to render boxes
- Parameter - Pos (`pos`): The position which will be used for the first and second corner of the box
- Example:
```kotlin
new BoxShape(new Pos(0, 0, 0));
```

### `new BoxShape(pos1, pos2)`
- Description: Creates a new box shape, this is used to render boxes
- Parameters:
  - Pos (`pos1`): The position of the first corner of the box
  - Pos (`pos2`): The position of the second corner of the box
- Example:
```kotlin
new BoxShape(new Pos(0, 0, 0), new Pos(10, 10, 10));
```

### `new BoxShape(x, y, z)`
- Description: Creates a new box shape, this is used to render boxes
- Parameters:
  - Number (`x`): The x position which will be used for the first and second corner of the box
  - Number (`y`): The y position which will be used for the first and second corner of the box
  - Number (`z`): The z position which will be used for the first and second corner of the box
- Example:
```kotlin
new BoxShape(0, 0, 0);
```

### `new BoxShape(x1, y1, z1, x2, y2, z2)`
- Description: Creates a new box shape, this is used to render boxes
- Parameters:
  - Number (`x1`): The x position of the first corner of the box
  - Number (`y1`): The y position of the first corner of the box
  - Number (`z1`): The z position of the first corner of the box
  - Number (`x2`): The x position of the second corner of the box
  - Number (`y2`): The y position of the second corner of the box
  - Number (`z2`): The z position of the second corner of the box
- Example:
```kotlin
new BoxShape(0, 0, 0, 10, 10, 10);
```



# CentredShape class
CentredShape class for Arucas

This class represents shapes that are positioned centrally with a width
Import with `import CentredShape from Minecraft;`

## Methods

### `<CentredShape>.centrePositions()`
- Description: This centres the positions of the shape
- Example:
```kotlin
shape.centrePositions();
```

### `<CentredShape>.getPos()`
- Description: This gets the central position of the shape
- Returns - Pos: the central position of the shape
- Example:
```kotlin
shape.getPos();
```

### `<CentredShape>.getWidth()`
- Description: This gets the width of the shape
- Returns - Number: the width of the shape
- Example:
```kotlin
shape.getWidth();
```

### `<CentredShape>.setPos(pos)`
- Description: This sets the central position of the shape
- Parameter - Pos (`pos`): the central position of the shape
- Example:
```kotlin
shape.setPos(new Pos(1, 0, 100));
```

### `<CentredShape>.setWidth(width)`
- Description: This sets the width of the shape
- Parameter - Number (`width`): the width of the shape
- Example:
```kotlin
shape.setWidth(10.5);
```



# Collection class
Collection class for Arucas

This class is used to represent a collection of objects,
this class is used internally as the parent of maps, lists, and sets.
This cannot be instantiated directly.
All collections inherit Iterable, and thus can be iterated over
Class does not need to be imported

## Constructors

### `new Collection()`
- Description: This creates a collection, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildCollection: Collection {
    ChildCollection(): super();
    
    fun size() {
        return 0;
    }
}
```

## Methods

### `<Collection>.isEmpty()`
- Description: This allows you to check if the collection is empty
- Returns - Boolean: true if the collection is empty
- Example:
```kotlin
['object', 81, 96, 'case'].isEmpty(); // false
```

### `<Collection>.size()`
- Description: This allows you to get the size of the collection
- Returns - Number: the size of the list
- Example:
```kotlin
['object', 81, 96, 'case'].size();
```



# CommandBuilder class
CommandBuilder class for Arucas

This class allows you to build commands for Minecraft.
Import with `import CommandBuilder from Minecraft;`

## Methods

### `<CommandBuilder>.executes(function)`
- Description: This sets the function to be executed when the command is executed,
this should have the correct amount of parameters for the command
- Parameter - CommandBuilder (`function`): the function to execute
- Returns - CommandBuilder: the parent command builder
- Example:
```kotlin
commandBuilder.executes(fun() { });
```

### `<CommandBuilder>.then(childBuilder)`
- Description: This adds a child CommandBuilder to your command builder
- Parameter - CommandBuilder (`childBuilder`): the child command builder to add
- Returns - CommandBuilder: the parent command builder
- Example:
```kotlin
commandBuilder.then(CommandBuilder.literal('subcommand'));
```

## Static Methods

### `CommandBuilder.argument(argumentName, argumentType)`
- Description: Creates an argument builder with a specific argument type, and a name
to see all the different types refer to CommandBuilder.fromMap(...)
- Parameters:
  - String (`argumentName`): the name of the argument
  - String (`argumentType`): the type of the argument
- Returns - CommandBuilder: the argument builder
- Example:
```kotlin
CommandBuilder.argument('test', 'entityid');
```

### `CommandBuilder.argument(argumentName, argumentType, suggestions)`
- Description: Creates an argument builder with a specific argument type, a name, and a default value
to see all the different types refer to CommandBuilder.fromMap(...)
- Parameters:
  - String (`argumentName`): the name of the argument
  - String (`argumentType`): the type of the argument
  - List (`suggestions`): a list of strings for the suggestions for the argument
- Returns - CommandBuilder: the argument builder
- Example:
```kotlin
CommandBuilder.argument('test', 'word', ['wow', 'suggestion']);
```

### `CommandBuilder.fromMap(argumentMap)`
- Description: Creates an argument builder from a map.
The map must contain a 'name' key as a String that is the name of the command,
the map then can contain 'subcommands' as a map which contains the subcommands,
the key of the subcommands is the name of the subcommand, and the value is a map,
if the name is encased in '<' and '>' it will be treated as an argument, otherwise it will be treated as a literal.
You can chain arguments by leaving a space in the name like: 'literal <arg>'.
If the key has no name and is just an empty string the value will be used as the function
which will be executed when the command is executed, the function should have the appropriate
number of parameters, the number of parameters is determined by the number of arguments.
Argument types are defined in the main map under the key 'arguments' with the value of a map
the keys of this map should be the names of your arguments used in your subcommands,
this should be a map and must have the key 'type' which should be a string that is the type of the argument.
Optionally if the type is of 'integer' or 'double' you can also have the key 'min' and 'max' with numbers as the value,
and if the type is of 'enum' you must have the key 'enum' with the enum class type as the value: 'enum': MyEnum.type.
You can also optionally have 'suggests' which has the value of a list of strings that are suggestions for the argument.
You can also optionally have 'suggester' which has the value of a function that will be called to get suggestions for the argument,
this function should have arbitrary number of parameters which will be the arguments that the user has entered so far.
The possible argument types are: 'PlayerName', 'Word', 'GreedyString', 'Double', 'Integer', 'Boolean', 'Enum',
'ItemStack', 'Particle', 'RecipeId', 'EntityId', 'EnchantmentId'
- Parameter - Map (`argumentMap`): the map of arguments
- Returns - CommandBuilder: the argument builder
- Example:
```kotlin
effectCommandMap = {
    "name" : "effect",
    "subcommands" : {
        "give" : {
            "<targets> <effect>" : {
                "" : fun(target, effect) {
                    // do something
                },
                "<seconds>" : {
                    "" : fun(target, effect, second) {
                        // do something
                    },
                    "<amplifier>" : {
                        "" : fun(target, effect, second, amplifier) {
                            // do something
                        },
                        "<hideParticle>" : {
                            "" : fun(target, effect, second, amplifier, hideParticle) {
                                // do something
                            }
                        }
                    }
                }
            }
        },
        "clear" : {
            "" : fun() {
                // do something
            },
            "<targets>" : {
                "" : fun(target) {
                    // do something
                },
                "<effect>" : {
                    "" : fun(target, effect) {
                        // do something
                    }
                }
            }
        }
    },
    "arguments" : {
        "targets" : { "type" : "Entity" },
        "effect" : { "type" : "Effect", "suggests" : ["effect1", "effect2"] },
        "seconds" : { "type" : "Integer", "min" : 0, "max" : 1000000 },
        "amplifier" : { "type" : "Integer", "min" : 0, "max" : 255 },
        "hideParticle" : { "type" : "Boolean" }
    }
};
effectCommand = CommandBuilder.fromMap(effectCommandMap);
```

### `CommandBuilder.literal(argument)`
- Description: Creates a literal argument with just a string
- Parameter - String (`argument`): the literal argument
- Returns - CommandBuilder: the argument builder
- Example:
```kotlin
CommandBuilder.literal('test');
```



# Config class
Config class for Arucas

This class allows you to create configs for your scripts
Import with `import Config from Minecraft;`

## Methods

### `<Config>.addListener(listener)`
- Description: Adds a listener to the config, the listener will be called when the config is changed
The listener must have one parameter, this is the new value that was set
- Parameter - Function (`listener`): The listener to add
- Example:
```kotlin
config.addListener(function(newValue) {
    print(newValue);
});
```

### `<Config>.getCategory()`
- Description: Gets the category of the config
- Returns - String: The category of the config
- Example:
```kotlin
config.getCategory();
```

### `<Config>.getDefaultValue()`
- Description: Gets the default value of the config
- Returns - Object: The default value of the config
- Example:
```kotlin
config.getDefaultValue();
```

### `<Config>.getDescription()`
- Description: Gets the description of the config
- Returns - String: The description of the config
- Example:
```kotlin
config.getDescription();
```

### `<Config>.getName()`
- Description: Gets the name of the config
- Returns - String: The name of the config
- Example:
```kotlin
config.getName();
```

### `<Config>.getOptionalInfo()`
- Description: Gets the optional info of the config
- Returns - String: The optional info of the config
- Example:
```kotlin
config.getOptionalInfo();
```

### `<Config>.getType()`
- Description: Gets the type of the config
- Returns - String: The type of the config
- Example:
```kotlin
config.getType();
```

### `<Config>.getValue()`
- Description: Gets the value of the config
- Returns - Object: The value of the config
- Example:
```kotlin
config.getValue();
```

### `<Config>.resetToDefault()`
- Description: Resets the config to the default value
- Example:
```kotlin
config.resetToDefault();
```

### `<Config>.setValue(value)`
- Description: Sets the value of the config, if the value is invalid it will not be changed
- Parameter - Object (`value`): The new value of the config
- Example:
```kotlin
config.setValue(10);
```

### `<Config>.toJson()`
- Description: Converts the config into a json value, this will not keep the listeners
- Returns - Json: The config as a json value
- Example:
```kotlin
config.toJson();
```

## Static Methods

### `Config.fromListOfMap(list)`
- Description: Creates a config from a list of config maps
- Parameter - List (`list`): The list of config maps
- Returns - List: A list of configs created from the list of config maps
- Example:
```kotlin
configs = [
    {
        "type": "boolean",
        "name": "My Config",
        "description": "This is my config"
    },
    {
        "type": "cycle",
        "name": "My Cycle Config",
        "description": "This is my cycle config",
        "cycle_values": ["one", "two", "three"],
        "default_value": "two"
    }
];
configs = Config.fromListOfMap(configs);
```

### `Config.fromMap(map)`
- Description: Creates a config from a map
The map must contain the following keys:
'type' which is the type of the config which can be 'boolean', 'cycle', 'double', 'double_slider', 'integer', 'integer_slider', 'list', or 'string',
'name' which is the name of the config
And can optionally contain the following keys:
'description' which is a description of the config,
'optional_info' which is an optional info for the config,
'default_value' which is the default value of the config,
'category' which is the category of the config,
'value' which is the current value of the config, 
'listener' which is a function that will be called when the config changes, this must have 1 parameter which is the rule that was changed,
'max_length' which is the max length for the input of the config, this must be an integer > 0, default is 32
And 'cycle' types must contain the following keys:
'cycle_values' which is a list of values that the config can cycle through.
And slider types must contain the following keys:
'min' which is the minimum value of the slider,
'max' which is the maximum value of the slider
- Parameter - Map (`map`): The map to create the config from
- Returns - Config: The config created from the map
- Example:
```kotlin
configMap = {
    "type": "string",
    "name": "My Config",
    "description": "This is my config",
    "category": "Useful",
    "optional_info": "This is an optional info",
    "default_value": "foo",
    "value": "bar",
    "listener": fun(newValue) { },
    "max_length": 64
};
config = Config.fromMap(configMap);
```



# ConfigHandler class
ConfigHandler class for Arucas

This class allows you to easily read and write config files.
Import with `import ConfigHandler from Minecraft;`

## Constructors

### `new ConfigHandler(name)`
- Description: Creates a new ConfigHandler, this is used to read and save configs
- Parameter - String (`name`): The name of the config, this will also be the name of the config file
- Example:
```kotlin
new ConfigHandler('MyConfig');
```

### `new ConfigHandler(name, read)`
- Description: Creates a new ConfigHandler, this is used to read and save configs
- Parameters:
  - String (`name`): The name of the config, this will also be the name of the config file
  - Boolean (`read`): Whether or not to read the config on creation
- Example:
```kotlin
new ConfigHandler('MyConfig', false);
```

## Methods

### `<ConfigHandler>.addConfig(config)`
- Description: Adds a config to the handler
- Parameter - Config (`config`): The config to add
- Example:
```kotlin
config = Config.fromMap({
    "type": "boolean",
    "name": "My Config",
    "description": "This is my config"
});
configHandler.addConfig(config);
```

### `<ConfigHandler>.addConfigs(configs...)`
- Description: Adds multiple configs to the handler, you can pass in a list of configs
or a varargs of configs, this is for compatability with older scripts
- Parameter - Config (`configs...`): The configs to add
- Example:
```kotlin
config = Config.fromMap({
    "type": "boolean",
    "name": "My Config",
    "description": "This is my config"
});
configHandler.addConfigs(config, config);
```

### `<ConfigHandler>.createScreen()`
- Description: Creates a new config screen containing all of the configs in the handler, in alphabetical order.
The screen name will be the default, the same as the name of the config handler
- Returns - Screen: The new config screen
- Example:
```kotlin
configHandler.createScreen();
```

### `<ConfigHandler>.createScreen(title)`
- Description: Creates a new config screen containing all of the configs in the handler, in alphabetical order
- Parameter - Text (`title`): The title of the screen
- Returns - Screen: The new config screen
- Example:
```kotlin
configHandler.createScreen(Text.of('wow'));
```

### `<ConfigHandler>.createScreen(title, alphabetical)`
- Description: Creates a new config screen containing all of the configs in the handler
- Parameters:
  - Text (`title`): The title of the screen
  - Boolean (`alphabetical`): Whether or not to sort the configs alphabetically
- Returns - Screen: The new config screen
- Example:
```kotlin
configHandler.createScreen(Text.of('wow'), false);
```

### `<ConfigHandler>.getAllConfigs()`
- Description: Gets all the configs in the handler
- Returns - List: All the configs in the handler
- Example:
```kotlin
configHandler.getAllConfigs();
```

### `<ConfigHandler>.getConfig(name)`
- Description: Gets a config from the handler
- Parameter - String (`name`): The name of the config
- Returns - Config: The config
- Example:
```kotlin
configHandler.getConfig('MyConfig');
```

### `<ConfigHandler>.getName()`
- Description: Gets the name of the config
- Returns - String: The name of the config
- Example:
```kotlin
configHandler.getName();
```

### `<ConfigHandler>.read()`
- Description: Reads the all the configs from the file
If configs are already in the handler, only the values
will be overwritten
- Example:
```kotlin
configHandler.read();
```

### `<ConfigHandler>.removeConfig(name)`
- Description: Removes a config from the handler
- Parameter - String (`name`): The name of the config to remove
- Example:
```kotlin
configHandler.removeConfig('My Config');
```

### `<ConfigHandler>.resetAllToDefault()`
- Description: Resets all configs to their default values
- Example:
```kotlin
configHandler.resetAllToDefault();
```

### `<ConfigHandler>.save()`
- Description: Saves the configs to the file
- Example:
```kotlin
configHandler.save();
```

### `<ConfigHandler>.setSaveOnClose(saveOnClose)`
- Description: Sets whether or not the configs should be saved when the script ends, by default this is true
- Parameter - Boolean (`saveOnClose`): Whether or not the configs should be saved when the script ends
- Example:
```kotlin
configHandler.setSaveOnClose(false);
```

### `<ConfigHandler>.setSavePath(savePath)`
- Description: Sets the path to save the configs to, this shouldn't include the file name
- Parameter - File (`savePath`): The path to save the configs to
- Example:
```kotlin
configHandler.setSavePath(new File('/home/user/scripts/'));
```

### `<ConfigHandler>.willSaveOnClose()`
- Description: Gets whether or not the configs will be saved when the script ends
- Returns - Boolean: Whether or not the configs will be saved when the script ends
- Example:
```kotlin
configHandler.willSaveOnClose();
```



# CorneredShape class
CorneredShape class for Arucas

This class represents all shapes that use 2 corners to dictate their position
Import with `import CorneredShape from Minecraft;`

## Methods

### `<CorneredShape>.centrePositions()`
- Description: This centres the positions of the shape
- Example:
```kotlin
shape.centrePositions();
```

### `<CorneredShape>.getPos1()`
- Description: This gets the first position of the shape
- Returns - Pos: the first position of the shape
- Example:
```kotlin
shape.getPos1();
```

### `<CorneredShape>.getPos2()`
- Description: This gets the second position of the shape
- Returns - Pos: the second position of the shape
- Example:
```kotlin
shape.getPos2();
```

### `<CorneredShape>.setPos1(pos1)`
- Description: This sets the first position of the shape
- Parameter - Pos (`pos1`): the first position of the shape
- Example:
```kotlin
shape.setPos1(new Pos(1, 0, 100));
```

### `<CorneredShape>.setPos2(pos2)`
- Description: This sets the second position of the shape
- Parameter - Pos (`pos2`): the second position of the shape
- Example:
```kotlin
shape.setPos2(new Pos(1, 0, 100));
```



# Entity class
Entity class for Arucas

This class is mostly used to get data about entities.
Import with `import Entity from Minecraft;`

## Methods

### `<Entity>.canSpawnAt(pos)`
- Description: This checks whether the entity can spawn at given position with regard to light and hitbox
- Parameter - Pos (`pos`): the position to check
- Returns - Boolean: whether entity type can spawn at given position
- Example:
```kotlin
entity.canSpawnAt(new Pos(0,0,0));
```

### `<Entity>.collidesWith(pos, block)`
- Description: This checks whether the entity collides with a block at a given position
- Parameters:
  - Pos (`pos`): the position to check
  - Block (`block`): the block to check
- Returns - Boolean: whether the entity collides with the block
- Example:
```kotlin
entity.collidesWith(Pos.get(0, 0, 0), Block.of('minecraft:stone'));
```

### `<Entity>.getAge()`
- Description: This gets the age of the entity in ticks
- Returns - Number: the age of the entity in ticks
- Example:
```kotlin
entity.getAge();
```

### `<Entity>.getBiome()`
- Description: This gets the biome of the entity
- Returns - Biome: the biome the entity is in
- Example:
```kotlin
entity.getBiome();
```

### `<Entity>.getCustomName()`
- Description: This gets the custom name of the entity if it has one
- Returns - String: the custom name of the entity if it has one, otherwise null
- Example:
```kotlin
entity.getCustomName();
```

### `<Entity>.getDimension()`
- Description: This gets the dimension of the entity
- Returns - String: the dimension id of dimension the entity is in
- Example:
```kotlin
entity.getDimension();
```

### `<Entity>.getDistanceTo(otherEntity)`
- Description: This gets the distance between the entity and the other entity
- Parameter - Entity (`otherEntity`): the other entity
- Returns - Number: the distance between the entities
- Example:
```kotlin
entity.getDistanceTo(Player.get());
```

### `<Entity>.getEntityIdNumber()`
- Description: This gets the entity id number of the entity
- Returns - Number: the entity id number
- Example:
```kotlin
entity.getEntityIdNumber();
```

### `<Entity>.getEntityUuid()`
- Description: This gets the uuid of the entity
- Returns - String: the uuid of the entity
- Example:
```kotlin
entity.getEntityUuid();
```

### `<Entity>.getFullId()`
- Description: This gets the full id of the entity, this returns the full id, so for example
'minecraft:cow' you can find all entityNames on
[Joa's Entity Property Encyclopedia](https://joakimthorsen.github.io/MCPropertyEncyclopedia/entities.html)
- Returns - String: the full id of the entity
- Example:
```kotlin
entity.getFullId();
```

### `<Entity>.getHitbox()`
- Description: This gets the hitbox of the entity in a list containing the two corners of the hitbox, the minimum point and the maximum point
- Returns - List: the hitbox of the entity
- Example:
```kotlin
entity.getHitbox();
```

### `<Entity>.getId()`
- Description: This gets the id of the entity, this returns the id, so for examples 'cow'
- Returns - String: the id of the entity
- Example:
```kotlin
entity.getId();
```

### `<Entity>.getLookingAtBlock()`
- Description: This gets the block that the entity is currently looking at
with a max range of 20 blocks, if there is no block then it will return air
- Returns - Block: the block that the entity is looking at, containing the position
- Example:
```kotlin
entity.getLookingAtBlock();
```

### `<Entity>.getLookingAtBlock(maxDistance)`
- Description: This gets the block that the entity is currently looking at
with a specific max range, if there is no block then it will return air
- Parameter - Number (`maxDistance`): the max range to ray cast
- Returns - Block: the block that the entity is looking at, containing the position
- Example:
```kotlin
entity.getLookingAtBlock(10);
```

### `<Entity>.getLookingAtBlock(maxDistance, fluidType)`
- Description: This gets the block that the entity is currently looking at
with a specific max range, and optionally whether fluids should
be included, if there is no block then it will return air
- Parameters:
  - Number (`maxDistance`): the max range to ray cast
  - String (`fluidType`): the types of fluids to include, either 'none', 'sources', or 'all'
- Returns - Block: the block that the entity is looking at, containing the position
- Example:
```kotlin
entity.getLookingAtBlock(10, 'sources');
```

### `<Entity>.getLookingAtPos(maxDistance)`
- Description: This gets the position that the entity is currently looking at with a specific max range
- Parameter - Number (`maxDistance`): the max range to ray cast
- Returns - Pos: the position that the entity is looking at, containing the x, y, and z
- Example:
```kotlin
entity.getLookingAtPos(10);
```

### `<Entity>.getNbt()`
- Description: This gets the nbt of the entity as a map
- Returns - Map: the nbt of the entity
- Example:
```kotlin
entity.getNbt();
```

### `<Entity>.getPitch()`
- Description: This gets the pitch of the entity (vertical head rotation)
- Returns - Number: the pitch of the entity, between -90 and 90
- Example:
```kotlin
entity.getPitch();
```

### `<Entity>.getPos()`
- Description: This gets the position of the entity
- Returns - Pos: the position of the entity
- Example:
```kotlin
entity.getPos();
```

### `<Entity>.getSquaredDistanceTo(otherEntity)`
- Description: This gets the squared distance between the entity and the other entity
- Parameter - Entity (`otherEntity`): the other entity
- Returns - Number: the squared distance between the entities
- Example:
```kotlin
entity.getSquaredDistanceTo(Player.get());
```

### `<Entity>.getTranslatedName()`
- Description: This gets the translated name of the entity, for examples 'minecraft:pig' would return 'Pig' if your language is in english
- Returns - String: the translated name of the entity
- Example:
```kotlin
entity.getTranslatedName();
```

### `<Entity>.getVelocity()`
- Description: This gets the velocity of the entity in a list in the form [x, y, z]
- Returns - List: the velocity of the entity
- Example:
```kotlin
entity.getVelocity();
```

### `<Entity>.getWorld()`
- Description: This gets the world the entity is in
- Returns - World: the world the entity is in
- Example:
```kotlin
entity.getWorld();
```

### `<Entity>.getX()`
- Description: This gets the x position of the entity
- Returns - Number: the x position of the entity
- Example:
```kotlin
entity.getX();
```

### `<Entity>.getY()`
- Description: This gets the y position of the entity
- Returns - Number: the y position of the entity
- Example:
```kotlin
entity.getY();
```

### `<Entity>.getYaw()`
- Description: This gets the yaw of the entity (horizontal head rotation)
- Returns - Number: the yaw of the entity, between -180 and 180
- Example:
```kotlin
entity.getYaw();
```

### `<Entity>.getZ()`
- Description: This gets the z position of the entity
- Returns - Number: the z position of the entity
- Example:
```kotlin
entity.getZ();
```

### `<Entity>.isFalling()`
- Description: Returns true if the entity is falling
- Returns - Boolean: true if the entity is falling, false if not
- Example:
```kotlin
entity.isFalling();
```

### `<Entity>.isGlowing()`
- Description: Returns true if the entity is glowing
- Returns - Boolean: true if the entity is glowing, false if not
- Example:
```kotlin
entity.isGlowing();
```

### `<Entity>.isInLava()`
- Description: Returns true if the entity is in lava
- Returns - Boolean: true if the entity is in lava, false if not
- Example:
```kotlin
entity.isInLava();
```

### `<Entity>.isOf(entityId)`
- Description: This checks if the entity is of the given entity id
- Parameter - String (`entityId`): the entity id to check
- Returns - Boolean: true if the entity is of the given entity id
- Example:
```kotlin
entity.isOf('cow');
```

### `<Entity>.isOnFire()`
- Description: Returns true if the entity is on fire
- Returns - Boolean: true if the entity is on fire, false if not
- Example:
```kotlin
entity.isOnFire();
```

### `<Entity>.isOnGround()`
- Description: Returns true if the entity is on the ground
- Returns - Boolean: true if the entity is on the ground, false if not
- Example:
```kotlin
entity.isOnGround();
```

### `<Entity>.isSneaking()`
- Description: Returns true if the player is sneaking
- Returns - Boolean: true if the player is sneaking, false if not
- Example:
```kotlin
entity.isSneaking();
```

### `<Entity>.isSprinting()`
- Description: Returns true if the player is sprinting
- Returns - Boolean: true if the player is sprinting, false if not
- Example:
```kotlin
entity.isSprinting();
```

### `<Entity>.isSubmergedInWater()`
- Description: Returns true if the entity is submerged in water
- Returns - Boolean: true if the entity is submerged in water, false if not
- Example:
```kotlin
entity.isSubmergedInWater();
```

### `<Entity>.isTouchingWater()`
- Description: Returns true if the entity is touching water
- Returns - Boolean: true if the entity is touching water, false if not
- Example:
```kotlin
entity.isTouchingWater();
```

### `<Entity>.isTouchingWaterOrRain()`
- Description: Returns true if the entity is touching water or rain
- Returns - Boolean: true if the entity is touching water or rain, false if not
- Example:
```kotlin
entity.isTouchingWaterOrRain();
```

### `<Entity>.setGlowing(glowing)`
- Description: This sets the entity to either start glowing or stop glowing on the client
- Parameter - Boolean (`glowing`): the glowing state
- Example:
```kotlin
entity.setGlowing(true);
```

## Static Methods

### `Entity.of(entityId)`
- Description: This converts an entityId into an entity instance.
This will throw an error if the id is not valid.
- Parameter - String (`entityId`): the entityId to convert to an entity
- Returns - Entity: the entity instance from the id
- Example:
```kotlin
Entity.of('minecraft:pig');
```



# Enum class
Enum class for Arucas

This class is the super class of all enums in Arucas.
Enums cannot be instantiated or extended
Class does not need to be imported

## Methods

### `<Enum>.getName()`
- Description: This allows you to get the name of an enum value
- Returns - String: the name of the enum value
- Example:
```kotlin
enum.getName();
```

### `<Enum>.ordinal()`
- Description: This allows you to get the ordinal of the enum value
- Returns - Number: the ordinal of the enum value
- Example:
```kotlin
enum.ordinal();
```



# Error class
Error class for Arucas

This class is used for errors, and this is the only type that can be thrown.
You are able to extend this class to create your own error types
Class does not need to be imported

## Constructors

### `new Error()`
- Description: This creates a new Error value with no message
- Example:
```kotlin
new Error();
```

### `new Error(details)`
- Description: This creates a new Error value with the given details as a message
- Parameter - String (`details`): the details of the error
- Example:
```kotlin
new Error('This is an error');
```

### `new Error(details, value)`
- Description: This creates a new Error value with the given details as a message and the given value
- Parameters:
  - String (`details`): the details of the error
  - Object (`value`): the value that is related to the error
- Example:
```kotlin
new Error('This is an error', [1, 2, 3]);
```

## Methods

### `<Error>.getDetails()`
- Description: This returns the raw message of the error
- Returns - String: the details of the error
- Example:
```kotlin
error.getDetails();
```

### `<Error>.getStackTraceString()`
- Description: This prints the stack trace of this error
- Returns - String: the stack trace converted to a string
- Example:
```kotlin
error.getStackTraceString();
```

### `<Error>.getValue()`
- Description: This returns the value that is related to the error
- Returns - Object: the value that is related to the error
- Example:
```kotlin
error.getValue();
```



# FakeBlock class
FakeBlock class for Arucas

This class can be used to create fake blocks which can be rendered in the world.
Import with `import FakeBlock from Minecraft;`

## Constructors

### `new FakeBlock(block, pos)`
- Description: Creates a fake block with the given block and position
- Parameters:
  - Block (`block`): The block to use
  - Pos (`pos`): The position of the block
- Example:
```kotlin
new FakeBlock(Material.BEDROCK.asBlock(), new Pos(0, 0, 0));
```

## Methods

### `<FakeBlock>.getBlock()`
- Description: Gets the current block type of the fake block
- Returns - Block: The block type of the fake block
- Example:
```kotlin
fakeBlock.getBlock();
```

### `<FakeBlock>.getDirection()`
- Description: Gets the direction of the fake block
- Returns - String: The direction of the fake block, may be null
- Example:
```kotlin
fakeBlock.getDirection();
```

### `<FakeBlock>.getPos()`
- Description: Gets the position of the fake block
- Returns - Pos: The position of the fake block
- Example:
```kotlin
fakeBlock.getPos();
```

### `<FakeBlock>.setBlock(block)`
- Description: Sets the block type to render of the fake block
- Parameter - Block (`block`): The block to render
- Example:
```kotlin
fakeBlock.setBlock(Material.BEDROCK.asBlock());
```

### `<FakeBlock>.setCull(cull)`
- Description: Sets whether the fake block should be culled
- Parameter - Boolean (`cull`): Whether the fake block should be culled
- Example:
```kotlin
fakeBlock.setCull(true);
```

### `<FakeBlock>.setDirection(direction)`
- Description: Sets the direction of the fake block,
this may be null in which case the block will face the player
- Parameter - String (`direction`): The direction of the fake block
- Example:
```kotlin
fakeBlock.setDirection(Direction.UP);
```

### `<FakeBlock>.setPos(pos)`
- Description: Sets the position of the fake block
- Parameter - Pos (`pos`): The position of the fake block
- Example:
```kotlin
fakeBlock.setPos(new Pos(0, 0, 0));
```

### `<FakeBlock>.shouldCull()`
- Description: Gets whether the fake block should be culled
- Returns - Boolean: Whether the fake block should be culled
- Example:
```kotlin
fakeBlock.shouldCull();
```



# FakeEntity class
FakeEntity class for Arucas

This allows you to create a fake entity which can be rendered in the world.
Import with `import FakeEntity from Minecraft;`

## Constructors

### `new FakeEntity(entity, world)`
- Description: Creates a new fake entity
- Parameters:
  - Entity (`entity`): The entity that you want to create into a fake entity
  - World (`world`): The world that the entity is being rendered in
- Example:
```kotlin
fakeEntity = new FakeEntity();
```

## Methods

### `<FakeEntity>.despawn()`
- Description: Despawns the entity (makes it not render in the world)
- Example:
```kotlin
fakeEntity.despawn();
```

### `<FakeEntity>.getBodyYaw()`
- Description: Gets the body yaw of the entity
- Returns - Number: The body yaw of the entity
- Example:
```kotlin
bodyYaw = fakeEntity.getBodyYaw();
```

### `<FakeEntity>.getPitch()`
- Description: Gets the pitch of the entity
- Returns - Number: The pitch of the entity
- Example:
```kotlin
pitch = fakeEntity.getPitch();
```

### `<FakeEntity>.getPos()`
- Description: Gets the position of the entity
- Returns - Pos: The position of the entity
- Example:
```kotlin
pos = fakeEntity.getPos();
```

### `<FakeEntity>.getWorld()`
- Description: Gets the world that the entity is being rendered in
- Returns - World: The world that the entity is being rendered in
- Example:
```kotlin
world = fakeEntity.getWorld();
```

### `<FakeEntity>.getYaw()`
- Description: Gets the yaw of the entity
- Returns - Number: The yaw of the entity
- Example:
```kotlin
yaw = fakeEntity.getYaw();
```

### `<FakeEntity>.setBodyYaw(bodyYaw)`
- Description: Sets the body yaw of the entity with no interpolation
- Parameter - Number (`bodyYaw`): The new body yaw of the entity
- Example:
```kotlin
fakeEntity.setBodyYaw(0);
```

### `<FakeEntity>.setBodyYaw(bodyYaw, interpolationSteps)`
- Description: Sets the body yaw of the entity
- Parameters:
  - Number (`bodyYaw`): The new body yaw of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.setBodyYaw(0, 10);
```

### `<FakeEntity>.setPitch(pitch)`
- Description: Sets the pitch of the entity with no interpolation
- Parameter - Number (`pitch`): The new pitch of the entity
- Example:
```kotlin
fakeEntity.setPitch(0);
```

### `<FakeEntity>.setPitch(pitch, interpolationSteps)`
- Description: Sets the pitch of the entity
- Parameters:
  - Number (`pitch`): The new pitch of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.setPitch(0, 10);
```

### `<FakeEntity>.setPos(pos)`
- Description: Sets the position of the entity with no interpolation
- Parameter - Pos (`pos`): The new position of the entity
- Example:
```kotlin
fakeEntity.setPos(new Pos(0, 0, 0));
```

### `<FakeEntity>.setPos(pos, interpolationSteps)`
- Description: Sets the position of the entity
- Parameters:
  - Pos (`pos`): The new position of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.setPos(new Pos(0, 0, 0), 0);
```

### `<FakeEntity>.setWorld(world)`
- Description: Sets the world that the entity is being rendered in
- Parameter - World (`world`): The world that the entity is being rendered in
- Example:
```kotlin
fakeEntity.setWorld(MinecraftClient.getClient().getWorld());
```

### `<FakeEntity>.setYaw(yaw)`
- Description: Sets the yaw of the entity with no interpolation
- Parameter - Number (`yaw`): The new yaw of the entity
- Example:
```kotlin
fakeEntity.setYaw(0);
```

### `<FakeEntity>.setYaw(yaw, interpolationSteps)`
- Description: Sets the yaw of the entity
- Parameters:
  - Number (`yaw`): The new yaw of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.setYaw(0, 10);
```

### `<FakeEntity>.spawn()`
- Description: Spawns the entity (makes it render in the world)
- Example:
```kotlin
fakeEntity.spawn();
```

### `<FakeEntity>.updatePosAndRotation(pos, yaw, pitch)`
- Description: Updates the position and rotation of the entity with no interpolation
- Parameters:
  - Pos (`pos`): The new position of the entity
  - Number (`yaw`): The new yaw of the entity
  - Number (`pitch`): The new pitch of the entity
- Example:
```kotlin
fakeEntity.updatePosAndRotation(new Pos(100, 0, 100), 0, 0);
```

### `<FakeEntity>.updatePosAndRotation(pos, yaw, pitch, interpolationSteps)`
- Description: Updates the position and rotation of the entity
- Parameters:
  - Pos (`pos`): The new position of the entity
  - Number (`yaw`): The new yaw of the entity
  - Number (`pitch`): The new pitch of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.updatePosAndRotation(new Pos(100, 0, 100), 0, 0, 10);
```



# FakeScreen class
FakeScreen class for Arucas

This class extends Screen and so inherits all of their methods too,
this class is used to create client side inventory screens.
Import with `import FakeScreen from Minecraft;`

## Constructors

### `new FakeScreen(name, rows)`
- Description: Creates a FakeScreen instance with given name and given amount of rows,
this will throw an error if the rows are not between 1 and 6
- Parameters:
  - String (`name`): the name of the screen
  - Number (`rows`): the number of rows between 1 - 6
- Example:
```kotlin
new FakeScreen('MyScreen', 6);
```

## Methods

### `<FakeScreen>.getStackForSlot(slotNum)`
- Description: Gets the stack for the given slot, if the slot is out of bounds it returns null
- Parameter - Number (`slotNum`): the slot number
- Returns - ItemStack: the stack for the given slot
- Example:
```kotlin
fakeScreen.getStackForSlot(0);
```

### `<FakeScreen>.onClick(function)`
- Description: This sets the callback for when a slot is clicked in the inventory.
The callback must have 3 parameters, the first is the item stack that was clicked,
then second is the slot number, third is the action as a string
- Parameter - Function (`function`): the callback function
- Example:
```kotlin
fakeScreen.onClick(fun(item, slotNum, action) {
    // action can be any of the following:
    // 'PICKUP', 'QUICK_MOVE', 'SWAP', 'CLONE', 'THROW', 'QUICK_CRAFT', or 'PICKUP_ALL'
    print(action);
});
```

### `<FakeScreen>.setStackForSlot(slotNum, stack)`
- Description: Sets the stack for the given slot, if the slot is out of bounds it won't be set
- Parameters:
  - Number (`slotNum`): the slot number
  - ItemStack (`stack`): the stack to set
- Example:
```kotlin
fakeScreen.setStackForSlot(0, Material.DIAMOND_BLOCK.asItemStack());
```



# File class
File class for Arucas

This class allows you to read and write files
Class does not need to be imported

## Constructors

### `new File(path)`
- Description: This creates a new File object with set path
- Parameter - String (`path`): the path of the file
- Example:
```kotlin
new File('foo/bar/script.arucas');
```

## Methods

### `<File>.createDirectory()`
- Description: This creates all parent directories of the file if they don't already exist
- Returns - Boolean: true if the directories were created
- Example:
```kotlin
file.createDirectory();
```

### `<File>.delete()`
- Description: This deletes the file
- Returns - Boolean: true if the file was deleted
- Example:
```kotlin
file.delete();
```

### `<File>.exists()`
- Description: This returns if the file exists
- Returns - Boolean: true if the file exists
- Example:
```kotlin
file.exists();
```

### `<File>.getAbsolutePath()`
- Description: This returns the absolute path of the file
- Returns - String: the absolute path of the file
- Example:
```kotlin
file.getAbsolutePath();
```

### `<File>.getName()`
- Description: This returns the name of the file
- Returns - String: the name of the file
- Example:
```kotlin
File.getName();
```

### `<File>.getPath()`
- Description: This returns the path of the file
- Returns - String: the path of the file
- Example:
```kotlin
file.getPath();
```

### `<File>.getSubFiles()`
- Description: This returns a list of all the sub files in the directory
- Returns - List: a list of all the sub files in the directory
- Example:
```kotlin
file.getSubFiles();
```

### `<File>.open()`
- Description: This opens the file (as in opens it on your os)
- Example:
```kotlin
file.open();
```

### `<File>.read()`
- Description: This reads the file and returns the contents as a string
- Returns - String: the contents of the file
- Example:
```kotlin
file.read();
```

### `<File>.resolve(filePath)`
- Description: This gets a resolves file object from the current one
- Parameter - String (`filePath`): the relative file path
- Returns - File: the resolved file
- Example:
```kotlin
file.resolve('child.txt');
```

### `<File>.write(string)`
- Description: This writes a string to a file
- Parameter - String (`string`): the string to write to the file
- Example:
```kotlin
file.write('Hello World!');
```

## Static Methods

### `File.getDirectory()`
- Description: This returns the file of user directory
- Returns - File: the file of the working directory
- Example:
```kotlin
File.getDirectory();
```



# Function class
Function class for Arucas

This class is used for functions, and this is the only type that can be called.
You are able to extend this class and implement an 'invoke' method to create
your own function types, this class cannot be instantiated directly
Class does not need to be imported

## Constructors

### `new Function()`
- Description: This creates a function, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildFunction: Function {
    ChildFunction(): super();
}
```



# Future class
Future class for Arucas

This class is used to represent values that are in the future.
More precisely values that are being evaluated on another thread,
this allows you to access those values once they've been processed
Class does not need to be imported

## Methods

### `<Future>.await()`
- Description: This blocks the current thread until the future has
been completed and then returns the value of the future
- Returns - Object: The value of the future
- Example:
```kotlin
future.await();
```

### `<Future>.isComplete()`
- Description: This returns whether the future has been completed
- Returns - Boolean: Whether the future has been completed
- Example:
```kotlin
future.isComplete();
```

## Static Methods

### `Future.completed(value)`
- Description: This returns a future that with a complete value
- Parameter - Object (`value`): The value to complete the future with
- Returns - Future: The future that has been completed with the value
- Example:
```kotlin
future = Future.completed(true);
```



# GameEvent class
GameEvent class for Arucas

This class allows you to register listeners for game events in Minecraft.
Import with `import GameEvent from Minecraft;`

## Constructors

### `new GameEvent(eventName, onEvent)`
- Description: This creates a new GameEvent, that is not cancellable
- Parameters:
  - String (`eventName`): The name of the event, you can find these on the GameEvents page
  - Function (`onEvent`): The function to run when the event is called, some events may have parameters
- Example:
```kotlin
new GameEvent('onClientTick', fun() { });
```

### `new GameEvent(eventName, onEvent, cancellable)`
- Description: This creates a new GameEvent
- Parameters:
  - String (`eventName`): The name of the event, you can find these on the GameEvents page
  - Function (`onEvent`): The function to run when the event is called, some events may have parameters
  - Boolean (`cancellable`): Whether or not the event is cancellable, if it is then it will run on the main thread
- Example:
```kotlin
new GameEvent('onClientTick', fun() { }, true);
```

## Methods

### `<GameEvent>.isRegistered()`
- Description: This returns whether or not the event is registered
- Returns - Boolean: Whether or not the event is registered
- Example:
```kotlin
gameEvent.isRegistered();
```

### `<GameEvent>.register()`
- Description: This registers the event
- Example:
```kotlin
gameEvent.register();
```

### `<GameEvent>.unregister()`
- Description: This unregisters the event
- Example:
```kotlin
gameEvent.unregister();
```

## Static Methods

### `GameEvent.cancel()`
- Description: If called on a cancellable event, this will stop execution and cancel the event,
if called on a non-cancellable event, or not on an event, this will throw an error
- Example:
```kotlin
GameEvent.cancel();
```

### `GameEvent.future()`
- Description: This returns a future that allows you to wait for an event to occur
- Returns - Future: the future, will complete once the event has occurred
- Example:
```kotlin
GameEvent.future('onClientTick').await();
```

### `GameEvent.unregisterAll()`
- Description: This unregisters all events registered by this script
- Example:
```kotlin
GameEvent.unregisterAll();
```



# ItemEntity class
ItemEntity class for Arucas

This class extends Entity and so inherits all of their methods too,
ItemEntities are entities that are dropped items.
Import with `import ItemEntity from Minecraft;`

## Methods

### `<ItemEntity>.getCustomName()`
- Description: This method returns the custom name of the ItemEntity
- Returns - String: the custom name of the entity
- Example:
```kotlin
itemEntity.getCustomName();
```

### `<ItemEntity>.getItemAge()`
- Description: This method returns the age of the ItemEntity
this is increased every tick and the item entity despawns after 6000 ticks
- Returns - Number: the age of the entity
- Example:
```kotlin
itemEntity.getItemAge();
```

### `<ItemEntity>.getItemStack()`
- Description: This method returns the ItemStack that is held in the ItemEntity
- Returns - ItemStack: the ItemStack that the entity holds
- Example:
```kotlin
itemEntity.getItemStack();
```

### `<ItemEntity>.getThrower()`
- Description: This method returns the player that threw the ItemEntity, null if not thrown by a player or player not found
- Returns - Player: the player that threw the entity
- Example:
```kotlin
itemEntity.getThrower();
```



# ItemStack class
ItemStack class for Arucas

This class represents an item stack. It can be used to create new item stacks, or to modify existing ones.
Import with `import ItemStack from Minecraft;`

## Methods

### `<ItemStack>.asEntity()`
- Description: This creates an item entity with the item
- Returns - ItemEntity: the entity of the ItemStack
- Example:
```kotlin
itemStack.asEntity();
```

### `<ItemStack>.getCount()`
- Description: This gets the count of the ItemStack, the amount of items in the stack
- Returns - Number: the count of the ItemStack
- Example:
```kotlin
itemStack.getCount();
```

### `<ItemStack>.getCustomName()`
- Description: This gets the custom name of the ItemStack
- Returns - String: the custom name of the ItemStack
- Example:
```kotlin
itemStack.getCustomName();
```

### `<ItemStack>.getDurability()`
- Description: This gets the durability of the item
- Returns - Number: the durability of the item
- Example:
```kotlin
itemStack.getDurability();
```

### `<ItemStack>.getEnchantments()`
- Description: This gets the enchantments of the item, in a map containing the
id of the enchantment as the key and the level of the enchantment as the value
- Returns - Map: the enchantments of the item, map may be empty
- Example:
```kotlin
itemStack.getEnchantments();
```

### `<ItemStack>.getMaterial()`
- Description: This gets the material of the ItemStack
- Returns - Material: the material of the ItemStack
- Example:
```kotlin
itemStack.getMaterial();
```

### `<ItemStack>.getMaxCount()`
- Description: This gets the max stack size of the ItemStack
- Returns - Number: the max stack size of the ItemStack
- Example:
```kotlin
itemStack.getMaxCount();
```

### `<ItemStack>.getMaxDurability()`
- Description: This gets the max durability of the item
- Returns - Number: the max durability of the item
- Example:
```kotlin
itemStack.getMaxDurability();
```

### `<ItemStack>.getMiningSpeedMultiplier(block)`
- Description: This gets the mining speed multiplier of the ItemStack for the given Block,
for example a diamond pickaxe on stone would have a higher multiplier than air on stone
- Parameter - Block (`block`): the Block to get the mining speed multiplier for
- Returns - Number: the mining speed multiplier of the ItemStack for the given Block
- Example:
```kotlin
pickaxe = Material.DIAMOND_PICKAXE.asItemStack();
goldBlock = Material.GOLD_BLOCK.asBlock();

pickaxe.getMiningSpeedMultiplier(goldBlock);
```

### `<ItemStack>.getNbt()`
- Description: This gets the NBT data of the ItemStack as a Map
- Returns - Map: the NBT data of the ItemStack
- Example:
```kotlin
itemStack.getNbt();
```

### `<ItemStack>.getNbtAsString()`
- Description: This gets the NBT data of the ItemStack as a String
- Returns - String: the NBT data of the ItemStack
- Example:
```kotlin
itemStack.getNbtAsString();
```

### `<ItemStack>.getTranslatedName()`
- Description: This gets the translated name of the ItemStack, for example
'diamond_sword' would return 'Diamond Sword' if your language is English
- Returns - String: the translated name of the ItemStack
- Example:
```kotlin
itemStack.getTranslatedName();
```

### `<ItemStack>.isBlockItem()`
- Description: This checks if the ItemStack can be placed as a block
- Returns - Boolean: true if the ItemStack can be placed as a block, false otherwise
- Example:
```kotlin
itemStack.isBlockItem();
```

### `<ItemStack>.isNbtEqual(itemStack)`
- Description: This checks if the ItemStack has the same NBT data as the other given ItemStack
- Parameter - ItemStack (`itemStack`): the other ItemStack to compare to
- Returns - Boolean: true if the ItemStack has the same NBT data as the other given ItemStack
- Example:
```kotlin
itemStack.isNbtEqual(Material.GOLD_INGOT.asItemStack());
```

### `<ItemStack>.isStackable()`
- Description: This checks if the ItemStack is stackable
- Returns - Boolean: true if the ItemStack is stackable, false otherwise
- Example:
```kotlin
itemStack.isStackable();
```

### `<ItemStack>.setCustomName(customName)`
- Description: This sets the custom name of the ItemStack
- Parameter - Text (`customName`): the custom name of the ItemStack, this can be text or string
- Returns - ItemStack: the ItemStack with the new custom name
- Example:
```kotlin
itemStack.setCustomName('My Pickaxe');
```

### `<ItemStack>.setItemLore(lore)`
- Description: This sets the lore of the ItemStack
- Parameter - List (`lore`): the lore of the ItemStack as a list of Text
- Returns - ItemStack: the ItemStack with the new lore
- Example:
```kotlin
itemStack = Material.DIAMOND_PICKAXE.asItemStack();
itemStack.setItemLore([
    Text.of('This is a pickaxe'),
    Text.of('It is made of diamond')
]);
```

### `<ItemStack>.setNbt(nbtMap)`
- Description: This sets the NBT data of the ItemStack
- Parameter - Map (`nbtMap`): the NBT data of the ItemStack as a map
- Returns - ItemStack: the ItemStack with the new NBT data
- Example:
```kotlin
itemStack.setNbt({'Lore': []});
```

### `<ItemStack>.setNbtFromString(nbtString)`
- Description: This sets the NBT data of the ItemStack from an NBT string
- Parameter - String (`nbtString`): the NBT data of the ItemStack as a string
- Returns - ItemStack: the ItemStack with the new NBT data
- Example:
```kotlin
itemStack.setNbtFromString("{\"Lore\": []}");
```

### `<ItemStack>.setStackSize(stackSize)`
- Description: This sets the stack size of the ItemStack
- Parameter - Number (`stackSize`): the stack size of the ItemStack
- Returns - ItemStack: the ItemStack with the new stack size
- Example:
```kotlin
itemStack.setStackSize(5);
```

## Static Methods

### `ItemStack.of(material)`
- Description: This creates an ItemStack from a material or a string
- Parameter - Material (`material`): the material, item stack, block, or string to create the ItemStack from
- Returns - ItemStack: the new ItemStack instance
- Example:
```kotlin
ItemStack.of('dirt');
```

### `ItemStack.parse(nbtString)`
- Description: This creates an ItemStack from a NBT string, this can be in the form of a map
or an ItemStack NBT, or like the item stack command format
- Parameter - String (`nbtString`): the NBT string to create the ItemStack from
- Returns - ItemStack: the new ItemStack instance
- Example:
```kotlin
ItemStack.parse('{id:"minecraft:dirt",Count:64}')
```



# Iterable class
Iterable class for Arucas

This class represents an object that can be iterated over.
This class is used internally to denote whether an object can be
iterated over inside a foreach loop
Class does not need to be imported

## Constructors

### `new Iterable()`
- Description: This creates an iterable, this cannot be called directly, only from child classes
- Example:
```kotlin
class IterableImpl: Iterable {
    IterableImpl(): super();
    
    fun iterator() {
        // Example
        return [].iterator();
    }
}
```

## Methods

### `<Iterable>.iterator()`
- Description: This gets the generated iterator
- Returns - Iterator: the generated iterator
- Example:
```kotlin
iterable = [];
i = iterable.iterator();
while (i.hasNext()) {
    next = i.next();
}

// Or just, compiles to above
foreach (next : iterable); 
```



# Iterator class
Iterator class for Arucas

This class represents an object that iterates.
This is what is used internally to iterate in a
foreach loop and you can create your own iterators
to use be able to use them inside a foreach
Class does not need to be imported

## Constructors

### `new Iterator()`
- Description: This creates an iterator, this cannot be called directly, only from child classes
- Example:
```kotlin
class IteratorImpl: Iterator {
    IteratorImpl(): super();
    
    fun hasNext() {
        return false;
    }
    
    fun next() {
        throw new Error("Nothing next");
    }
}
```

## Methods

### `<Iterator>.hasNext()`
- Description: Checks whether the iterator has a next item to iterate
- Returns - Boolean: whether there are items left to iterate
- Example:
```kotlin
iterator = [].iterator();
iterator.hasNext();
```

### `<Iterator>.next()`
- Description: Gets the next item in the iterator, may throw if there is no next item
- Returns - Object: the next item
- Example:
```kotlin
iterator = [10, 20].iterator();
iterator.next(); // 10
iterator.next(); // 20
```



# Java class
Java class for Arucas

This class wraps Java values allowing for interactions between Java and Arucas.
This class cannot be instantiated or extended but you can create Java values by
using the static method 'Java.valueOf()' to convert Arucas to Java
Import with `import Java from util.Internal;`

## Methods

### `<Java>.callMethod(methodName, parameters...)`
- Deprecated: You should call the method directly on the value: Java.valueOf('').isBlank();
- Description: This calls the specified method with the specified parameters, calling the method
with this function has no benefits unless you are calling a function that also is
native to Arucas. For example `object.copy()` will use the Arucas 'copy' function.
But this is extremely rare so almost all of the time you should all the method normally.
- Parameters:
  - String (`methodName`): the name of the method
  - Object (`parameters...`): the parameters to call the method with
- Returns - Java: the return value of the method call wrapped in the Java wrapper
- Example:
```kotlin
Java.valueOf('').callMethod('isBlank');
```

### `<Java>.getField(fieldName)`
- Deprecated: You should call the method directly on the value: `Java.constructClass('me.senseiwells.impl.Test').A;`
- Description: This returns the Java wrapped value of the specified field.
There is no reason for you to be using this method, it will be removed in future versions
- Parameter - String (`fieldName`): the name of the field
- Returns - Java: the Java wrapped value of the field
- Example:
```kotlin
Java.constructClass('me.senseiwells.impl.Test').getField('A');
```

### `<Java>.getMethodDelegate(methodName, parameters)`
- Deprecated: Consider wrapping the method in a lambda instead
- Description: This returns a method delegate for the specified method name and parameters.
This should be avoided and replaced with a Arucas function wrapping the call instead.
For example: `delegate = (fun() { Java.valueOf('').isBlank(); });`.
Another thing to note is that the parameter count parameter is no longer
used and ignored internally, instead the parameters are calculated when you
call the delegate. The parameter remains for backwards compatability.
- Parameters:
  - String (`methodName`): the name of the method
  - Number (`parameters`): the number of parameters
- Returns - Function: the function containing the Java method delegate
- Example:
```kotlin
Java.valueOf('string!').getMethodDelegate('isBlank', 0);
```

### `<Java>.setField(fieldName, value)`
- Deprecated: You should assign the value directly on the value: Java.constructClass('me.senseiwells.impl.Test').A = 'Hello';
- Description: This sets the specified field to the specified value
There is no reason for you to be using this method, it will be removed in future versions
- Parameters:
  - String (`fieldName`): the name of the field
  - Object (`value`): the value to set the field to, the value type must match the type of the field
- Example:
```kotlin
Java.constructClass('me.senseiwells.impl.Test').setField('A', 'Hello');
```

### `<Java>.toArucas()`
- Description: This converts the Java value to an Arucas Value if possible, this may still
be of a Java value if it cannot be converted. For example, Strings, Numbers, Lists
will be converted but 
- Returns - Object: the Value in Arucas, this may still be of Java value if the value cannot be converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted
- Example:
```kotlin
Java.valueOf([1, 2, 3]).toArucas();
```

## Static Methods

### `Java.arrayOf(values...)`
- Description: Creates a Java Object array with a given values, this will be the size of the array,
this cannot be used to create primitive arrays
- Parameter - Object (`values...`): the values to add to the array
- Returns - Java: the Java Object array
- Example:
```kotlin
Java.arrayOf(1, 2, 3, 'string!', false);
```

### `Java.booleanArray(size)`
- Description: Creates a Java boolean array with a given size, the array is filled with false
by default and can be filled with only booleans
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java boolean array
- Example:
```kotlin
Java.booleanArray(10);
```

### `Java.booleanOf(bool)`
- Description: Creates a Java value boolean, to be used in Java
- Parameter - Boolean (`bool`): the boolean to convert to a Java boolean
- Returns - Java: the boolean in Java wrapper
- Example:
```kotlin
Java.booleanOf(true);
```

### `Java.byteArray(size)`
- Description: Creates a Java byte array with a given size, the array is filled with 0's
by default and can be filled with only bytes
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java byte array
- Example:
```kotlin
Java.byteArray(10);
```

### `Java.byteOf(num)`
- Description: Creates a Java value byte, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java byte
- Returns - Java: the byte in Java wrapper
- Example:
```kotlin
Java.byteOf(1);
```

### `Java.callStaticMethod(className, methodName, parameters...)`
- Deprecated: You should use 'Java.classOf(name)' then call the static method
- Description: Calls a static method of a Java class.
This should be avoided and instead use 'classOf' to get the
instance of the class then call the static method on that
- Parameters:
  - String (`className`): the name of the class
  - String (`methodName`): the name of the method
  - Object (`parameters...`): any parameters to call the method with
- Returns - Java: the return value of the method wrapped in the Java wrapper
- Example:
```kotlin
Java.callStaticMethod('java.lang.Integer', 'parseInt', '123');
```

### `Java.charArray(size)`
- Description: Creates a Java char array with a given size, the array is filled with null characters's
by default and can be filled with only chars
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java char array
- Example:
```kotlin
Java.charArray(10);
```

### `Java.charOf(char)`
- Description: Creates a Java value char, to be used in Java
- Parameter - String (`char`): the char to convert to a Java char
- Returns - Java: the char in Java wrapper
- Example:
```kotlin
Java.charOf('a');
```

### `Java.classFromName(className)`
- Deprecated: You should use 'Java.classOf(name)' instead
- Description: Gets a Java class from the name of the class
- Parameter - String (`className`): the name of the class you want to get
- Returns - Java: the Java Class<?> value wrapped in the Java wrapper
- Example:
```kotlin
Java.classFromName('java.util.ArrayList');
```

### `Java.classOf(className)`
- Description: Gets a Java class from the name of the class
- Parameter - String (`className`): the name of the class you want to get
- Returns - JavaClass: the Java class value which can be used as a class reference
- Example:
```kotlin
Java.classOf('java.util.ArrayList');
```

### `Java.constructClass(className, parameters...)`
- Deprecated: You should use 'Java.classOf(name)' then call the result to construct the class
- Description: This constructs a Java class with specified class name and parameters.
This should be avoided and instead use 'classOf' to get the class
instance then call the constructor on that instance
- Parameters:
  - String (`className`): the name of the class
  - Object (`parameters...`): any parameters to pass to the constructor
- Returns - Java: the constructed Java Object wrapped in the Java wrapper
- Example:
```kotlin
Java.constructClass('java.util.ArrayList');
```

### `Java.consumerOf(function)`
- Description: Creates a Java Consumer object from a given function, it must have one
parameter and any return values will be ignored
- Parameter - Function (`function`): the function to be executed
- Returns - Java: the Java Consumer object
- Example:
```kotlin
Java.consumerOf(fun(something) {
    print(something);
});
```

### `Java.doubleArray(size)`
- Description: Creates a Java double array with a given size, the array is filled with 0's
by default and can be filled with only doubles
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java double array
- Example:
```kotlin
Java.doubleArray(10);
```

### `Java.doubleOf(num)`
- Description: Creates a Java value double, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java double
- Returns - Java: the double in Java wrapper
- Example:
```kotlin
Java.doubleOf(1.0);
```

### `Java.floatArray(size)`
- Description: Creates a Java float array with a given size, the array is filled with 0's
by default and can be filled with only floats
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java float array
- Example:
```kotlin
Java.floatArray(10);
```

### `Java.floatOf(num)`
- Description: Creates a Java value float, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java float
- Returns - Java: the float in Java wrapper
- Example:
```kotlin
Java.floatOf(1.0);
```

### `Java.functionOf(function)`
- Description: Creates a Java Function object from a given function
- Parameter - Function (`function`): the function to be executed, this must have one parameter and must return a value
- Returns - Java: the Java Function object
- Example:
```kotlin
Java.functionOf(fun(something) {
    return something;
});
```

### `Java.getStaticField(className, fieldName)`
- Deprecated: You should use 'Java.classOf(name)' then access the static field
- Description: Gets a static field Java value from a Java class
- Parameters:
  - String (`className`): the name of the class
  - String (`fieldName`): the name of the field
- Returns - Java: the Java value of the field wrapped in the Java wrapper
- Example:
```kotlin
Java.getStaticField('java.lang.Integer', 'MAX_VALUE');
```

### `Java.getStaticMethodDelegate(className, methodName, parameters)`
- Deprecated: You should use 'Java.classOf(name)' then wrap the static method
- Description: Gets a static method delegate from a Java class, this should
be avoided and instance use 'classOf' to get the class instance
and then call the method on that class instance. The parameter count
parameter is no longer used internally but remains for backwards compatibility
- Parameters:
  - String (`className`): the name of the class
  - String (`methodName`): the name of the method
  - Number (`parameters`): the number of parameters
- Returns - Function: the delegated Java method in an Arucas Function
- Example:
```kotlin
Java.getStaticMethodDelegate('java.lang.Integer', 'parseInt', 1);
```

### `Java.intArray(size)`
- Description: Creates a Java int array with a given size, the array is filled with 0's
by default and can be filled with only ints
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java int array
- Example:
```kotlin
Java.intArray(10);
```

### `Java.intOf(num)`
- Description: Creates a Java value int, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java int
- Returns - Java: the int in Java wrapper
- Example:
```kotlin
Java.intOf(1);
```

### `Java.longArray(size)`
- Description: Creates a Java long array with a given size, the array is filled with 0's
by default and can be filled with only longs
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java long array
- Example:
```kotlin
Java.longArray(10);
```

### `Java.longOf(num)`
- Description: Creates a Java value long, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java long
- Returns - Java: the long in Java wrapper
- Example:
```kotlin
Java.longOf(1);
```

### `Java.objectArray(size)`
- Description: Creates a Java Object array with a given size, the array is filled with null values
by default and can be filled with any Java values, this array cannot be expanded
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java Object array
- Example:
```kotlin
Java.arrayWithSize(10);
```

### `Java.predicateOf(function)`
- Description: Creates a Java Predicate object from a given function
- Parameter - Function (`function`): the function to be executed, this must have one parameter and must return a boolean
- Returns - Java: the Java Predicate object
- Example:
```kotlin
Java.predicateOf(fun(something) {
    return something == 'something';
});
```

### `Java.runnableOf(function)`
- Description: Creates a Java Runnable object from a given function, this must
have no paramters and any return values will be ignored
- Parameter - Function (`function`): the function to be executed
- Returns - Java: the Java Runnable object
- Example:
```kotlin
Java.runnableOf(fun() {
    print('runnable');
});
```

### `Java.setStaticField(className, fieldName, newValue)`
- Deprecated: You should use 'Java.classOf(name)' then assign the static field
- Description: Sets a static field in a Java class with a new value
- Parameters:
  - String (`className`): the name of the class
  - String (`fieldName`): the name of the field
  - Object (`newValue`): the new value
- Example:
```kotlin
// Obviously this won't work, but it's just an example
Java.setStaticField('java.lang.Integer', 'MAX_VALUE', Java.intOf(100));"
```

### `Java.shortArray(size)`
- Description: Creates a Java short array with a given size, the array is filled with 0's
by default and can be filled with only shorts
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java short array
- Example:
```kotlin
Java.shortArray(10);
```

### `Java.shortOf(num)`
- Description: Creates a Java value short, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java short
- Returns - Java: the short in Java wrapper
- Example:
```kotlin
Java.shortOf(1);
```

### `Java.supplierOf(function)`
- Description: Creates a Java Supplier object from a given function
- Parameter - Function (`function`): the function to be executed, this must have no parameters and must return (supply) a value
- Returns - Java: the Java Supplier object
- Example:
```kotlin
Java.supplierOf(fun() {
    return 'supplier';
});
```

### `Java.valueOf(value)`
- Description: Converts any Arucas value into a Java value then wraps it in the Java wrapper and returns it
- Parameter - Object (`value`): any value to get the Java value of
- Returns - Java: the Java wrapper value, null if argument was null
- Example:
```kotlin
Java.valueOf('Hello World!');
```



# JavaClass class
JavaClass class for Arucas

This class 'acts' as a Java class. You are able to call this class which
will invoke the Java class' constructor, and access and assign the static
fields of the class. This class cannot be instantiated or extended.
Import with `import JavaClass from util.Internal;`



# Json class
Json class for Arucas

This class allows you to create and manipulate JSON objects.
This class cannot be instantiated or extended
Import with `import Json from util.Json;`

## Methods

### `<Json>.getValue()`
- Description: This converts the Json back into an object
- Returns - Object: the Value parsed from the Json
- Example:
```kotlin
json.getValue();
```

### `<Json>.writeToFile(file)`
- Description: This writes the Json to a file
if the file given is a directory or cannot be
written to, an error will be thrown
- Parameter - File (`file`): the file that you want to write to
- Example:
```kotlin
json.writeToFile(new File('D:/cool/realDirectory'));
```

## Static Methods

### `Json.fromFile(file)`
- Description: This will read a file and parse it into a Json, this will throw an error if the file cannot be read
- Parameter - File (`file`): the file that you want to parse into a Json
- Returns - Json: the Json parsed from the file
- Example:
```kotlin
Json.fromFile(new File('this/path/is/an/example.json'));
```

### `Json.fromList(list)`
- Description: This converts a list into a Json, an important thing to note is that
any values that are not Numbers, Booleans, Lists, Maps, or Null will use their
toString() member to convert them to a string
- Parameter - List (`list`): the list that you want to parse into a Json
- Returns - Json: the Json parsed from the list
- Example:
```kotlin
Json.fromList(['value', 1, true]);
```

### `Json.fromMap(map)`
- Description: This converts a map into a Json, an important thing to note is that
any values that are not Numbers, Booleans, Lists, Maps, or Null will use their
toString() member to convert them to a string
- Parameter - Map (`map`): the map that you want to parse into a Json
- Returns - Json: the Json parsed from the map
- Example:
```kotlin
Json.fromMap({'key': ['value1', 'value2']});
```

### `Json.fromString(string)`
- Description: This converts a string into a Json provided it is formatted correctly,
otherwise throwing an error
- Parameter - String (`string`): the string that you want to parse into a Json
- Returns - Json: the Json parsed from the string
- Example:
```kotlin
Json.fromString('{"key":"value"}');
```



# KeyBind class
KeyBind class for Arucas

This class allows you to create key binds that can be used, everything is
handled for you internally so you just need to regers the key bind and
the function you want to run when it is pressed.
Class does not need to be imported

## Constructors

### `new KeyBind(keyName)`
- Description: Creates a new key bind
- Parameter - String (`keyName`): the name of the key
- Example:
```kotlin
new KeyBind('MyKey');
```

## Methods

### `<KeyBind>.getKey()`
- Description: Gets the key bind's first key
- Returns - String: the key bind's key
- Example:
```kotlin
keyBind.getKey();
```

### `<KeyBind>.getKeys()`
- Description: Gets the all of the keys in the key bind
- Returns - List: list of strings of all the keys
- Example:
```kotlin
keybind.getKeys();
```

### `<KeyBind>.setCallback(callback)`
- Description: Sets the callback function for the key bind
- Parameter - Function (`callback`): the callback function
- Example:
```kotlin
keyBind.setCallback(fun() { print('My key was pressed'); });
```

### `<KeyBind>.setKey(keyName)`
- Description: Sets the key bind to a new key
- Parameter - String (`keyName`): the name of the key
- Example:
```kotlin
keyBind.setKey('f');
```

### `<KeyBind>.setKeys(keyNames...)`
- Description: Sets the key bind to new keys, you may also pass
in a list as the parameter, this is to keep compatability
- Parameter - String (`keyNames...`): the names of keys
- Example:
```kotlin
keyBind.setKeys('control', 'f');
```



# LineShape class
LineShape class for Arucas

This class allows you to create a line shape which can be used to draw lines in the world.
Import with `import LineShape from Minecraft;`

## Constructors

### `new LineShape(pos1, pos2)`
- Description: Creates a new line shape
- Parameters:
  - Pos (`pos1`): The starting position of the line
  - Pos (`pos2`): The ending position of the line
- Example:
```kotlin
new LineShape(new Pos(0, 0, 0), new Pos(1, 1, 1));
```

### `new LineShape(x1, y1, z1, x2, y2, z2)`
- Description: Creates a new line shape
- Parameters:
  - Number (`x1`): The x position of the starting position of the line
  - Number (`y1`): The y position of the starting position of the line
  - Number (`z1`): The z position of the starting position of the line
  - Number (`x2`): The x position of the ending position of the line
  - Number (`y2`): The y position of the ending position of the line
  - Number (`z2`): The z position of the ending position of the line
- Example:
```kotlin
new LineShape(0, 0, 0, 1, 1, 1);
```



# List class
List class for Arucas

This class is used for collections of ordered elements
Class does not need to be imported

## Constructors

### `new List()`
- Description: This creates a list, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildList: List {
    ChildList(): super();
}
```

## Methods

### `<List>.addAll(collection)`
- Description: This allows you to add all the values in another collection to the list
- Parameter - Collection (`collection`): the collection you want to add to the list
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].addAll(['foo', 'object']); // ['object', 81, 96, 'case', 'foo', 'object']
```

### `<List>.append(value)`
- Description: This allows you to append a value to the end of the list
- Parameter - Object (`value`): the value you want to append
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].append('foo'); // ['object', 81, 96, 'case', 'foo']
```

### `<List>.clear()`
- Description: This allows you to clear the list
- Example:
```kotlin
['object', 81, 96, 'case'].clear(); // []
```

### `<List>.contains(value)`
- Description: This allows you to check if the list contains a specific value
- Parameter - Object (`value`): the value you want to check
- Returns - Boolean: true if the list contains the value
- Example:
```kotlin
['object', 81, 96, 'case'].contains('case'); // true
```

### `<List>.containsAll(collection)`
- Description: This allows you to check if the list contains all the values in another collection
- Parameter - Collection (`collection`): the collection you want to check agains
- Returns - Boolean: true if the list contains all the values in the collection
- Example:
```kotlin
['object', 81, 96, 'case'].containsAll(['foo', 'object']); // false
```

### `<List>.filter(predicate)`
- Description: This filters the list using the predicate, a function that either returns
true or false, based on the element on whether it should be kept or not,
and returns a new list with the filtered elements
- Parameter - Function (`predicate`): a function that takes a value and returns Boolean
- Returns - List: the filtered collection
- Example:
```kotlin
(list = [1, 2, 3]).filter(fun(v) {
    return v > 1;
});
// list = [2, 3]
```

### `<List>.flatten()`
- Description: If there are any objects in the list that are collections they will
be expanded and added to the list. However collections inside those
collections will not be flattened, this is returned as a new list
- Returns - List: the flattened list
- Example:
```kotlin
(list = [1, 2, 3, [4, 5], [6, [7]]]).flatten();
// list = [1, 2, 3, 4, 5, 6, [7]]
```

### `<List>.get(index)`
- Description: This allows you to get the value at a specific index, alternative to bracket accessor,
this will throw an error if the index given is out of bounds
- Parameter - Number (`index`): the index of the value you want to get
- Returns - Object: the value at the index
- Example:
```kotlin
['object', 81, 96, 'case'].get(1); // 81
```

### `<List>.indexOf(value)`
- Description: This allows you to get the index of a specific value
- Parameter - Object (`value`): the value you want to get the index of
- Returns - Number: the index of the value
- Example:
```kotlin
['object', 81, 96, 'case', 81].indexOf(81); // 1
```

### `<List>.insert(value, index)`
- Description: This allows you to insert a value at a specific index, this will throw an error if the index is out of bounds
- Parameters:
  - Object (`value`): the value you want to insert
  - Number (`index`): the index you want to insert the value at
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].insert('foo', 1); // ['object', 'foo', 81, 96, 'case']
```

### `<List>.lastIndexOf(value)`
- Description: This allows you to get the last index of a specific value
- Parameter - Object (`value`): the value you want to get the last index of
- Returns - Number: the last index of the value
- Example:
```kotlin
['object', 81, 96, 'case', 96].lastIndexOf(96); // 4
```

### `<List>.map(mapper)`
- Description: This maps the list using the mapper, a function that takes a value and
returns a new value, and returns a new list with the mapped elements
- Parameter - Function (`mapper`): a function that takes a value and returns a new value
- Returns - List: the mapped collection
- Example:
```kotlin
(list = [1, 2, 3]).map(fun(v) {
    return v * 2;
});
// list = [2, 4, 6]
```

### `<List>.prepend(value)`
- Description: This allows you to prepend a value to the beginning of the list
- Parameter - Object (`value`): the value you want to prepend
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96].prepend('foo'); // ['foo', 'object', 81, 96]
```

### `<List>.reduce(reducer)`
- Description: This reduces the list using the reducer, a function that takes an
accumulated value and a new value and returns the next accumulated value
- Parameter - Function (`reducer`): a function that takes a value and returns a new value
- Returns - Object: the reduced value
- Example:
```kotlin
// a will start at 1 and b at 2
// next accumulator will be 3
// a will be 3 and b will be 3 = 6
(list = [1, 2, 3]).reduce(fun(a, b) {
    return a + b;
});
// 6
```

### `<List>.remove(index)`
- Description: This allows you to remove the value at a specific index, alternative to bracket assignment.
This will throw an error if the index is out of bounds
- Parameter - Number (`index`): the index of the value you want to remove
- Returns - Object: the value that was removed
- Example:
```kotlin
['object', 81, 96, 'case'].remove(1); // 81
```

### `<List>.removeAll(collection)`
- Description: This allows you to remove all the values in another collection from the list
- Parameter - Collection (`collection`): the collection you want to remove from the list
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].removeAll(['foo', 'object']); // [81, 96, 'case']
```

### `<List>.retainAll(list)`
- Description: This allows you to retain only the values that are in both lists
- Parameter - List (`list`): the list you want to retain values from
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].retainAll(['case', 'object', 54]); // ['object', 'case']
```

### `<List>.reverse()`
- Description: This allows you to reverse the list
- Returns - List: the reversed list
- Example:
```kotlin
['a', 'b', 'c', 'd'].reverse(); // ['d', 'c', 'b', 'a']
```

### `<List>.set(value, index)`
- Description: This allows you to set the value at a specific index, alternative to bracket assignment,
this will throw an erroor if the index given is out of bounds
- Parameters:
  - Object (`value`): the value you want to set
  - Number (`index`): the index you want to set the value at
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].set('foo', 1); // ['object', 'foo', 96, 'case']
```

### `<List>.shuffle()`
- Description: This allows you to shuffle the list
- Returns - List: the shuffled list
- Example:
```kotlin
['a', 'b', 'c', 'd'].shuffle(); // some random order ¯\_(ツ)_/¯
```

### `<List>.sort()`
- Description: This allows you to sort the list using the elements compare method
- Returns - List: the sorted list
- Example:
```kotlin
['d', 'a', 'c', 'b'].sort(); // ['a', 'b', 'c', 'd']
```

### `<List>.sort(comparator)`
- Description: This allows you to sort the list using a comparator function
- Parameter - Function (`comparator`): the comparator function
- Returns - List: the sorted list
- Example:
```kotlin
[6, 5, 9, -10].sort(fun(a, b) { return a - b; }); // [-10, 5, 6, 9]
```



# LivingEntity class
LivingEntity class for Arucas

This class extends Entity and so inherits all of their methods too,
LivingEntities are any entities that are alive, so all mobs
Import with `import LivingEntity from Minecraft;`

## Methods

### `<LivingEntity>.getHealth()`
- Description: This gets the LivingEntity's current health
- Returns - Number: the LivingEntity's health
- Example:
```kotlin
livingEntity.getHealth();
```

### `<LivingEntity>.getStatusEffects()`
- Description: This gets the LivingEntity's status effects, you can find
a list of all the ids of the status effects
[here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Effects)
- Returns - List: a list of status effects, may be empty
- Example:
```kotlin
livingEntity.getStatusEffects();
```

### `<LivingEntity>.isFlyFalling()`
- Description: This checks if the LivingEntity is fly falling (gliding with elytra)
- Returns - Boolean: true if the LivingEntity is fly falling
- Example:
```kotlin
livingEntity.isFlyFalling();
```



# Map class
Map class for Arucas

This class is used to create a map of objects, using keys and values.
This class cannot be directly instantiated, but can be extended to create a map of your own type.
Class does not need to be imported

## Constructors

### `new Map()`
- Description: This creates an empty map, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildMap: Map {
    ChildMap(): super();
}
```

## Methods

### `<Map>.clear()`
- Description: This allows you to clear the map of all the keys and values
- Example:
```kotlin
(map = {'key': 'value'}).clear(); // map = {}
```

### `<Map>.containsKey(key)`
- Description: This allows you to check if the map contains a specific key
- Parameter - Object (`key`): the key you want to check
- Returns - Boolean: true if the map contains the key, false otherwise
- Example:
```kotlin
{'key': 'value'}.containsKey('key'); // true
```

### `<Map>.containsValue(value)`
- Description: This allows you to check if the map contains a specific value
- Parameter - Object (`value`): the value you want to check
- Returns - Boolean: true if the map contains the value, false otherwise
- Example:
```kotlin
{'key': 'value'}.containsValue('foo'); // false
```

### `<Map>.get(key)`
- Description: This allows you to get the value of a key in the map
- Parameter - Object (`key`): the key you want to get the value of
- Returns - Object: the value of the key, will return null if non-existent
- Example:
```kotlin
{'key': 'value'}.get('key'); // 'value'
```

### `<Map>.getKeys()`
- Description: This allows you to get the keys in the map
- Returns - List: a complete list of all the keys
- Example:
```kotlin
{'key': 'value', 'key2': 'value2'}.getKeys(); // ['key', 'key2']
```

### `<Map>.getValues()`
- Description: This allows you to get the values in the map
- Returns - List: a complete list of all the values
- Example:
```kotlin
{'key': 'value', 'key2': 'value2'}.getValues(); // ['value', 'value2']
```

### `<Map>.map(remapper)`
- Description: This allows you to map the values in the map and returns a new map
- Parameter - Function (`remapper`): the function you want to map the values with
- Returns - Map: a new map with the mapped values
- Example:
```kotlin
map = {'key': 'value', 'key2': 'value2'}
map.map(fun(k, v) {
    return [v, k];
});
// map = {'value': 'key', 'value2': 'key2'}
```

### `<Map>.put(key, value)`
- Description: This allows you to put a key and value in the map
- Parameters:
  - Object (`key`): the key you want to put
  - Object (`value`): the value you want to put
- Returns - Object: the previous value associated with the key, null if none
- Example:
```kotlin
{'key': 'value'}.put('key2', 'value2'); // null
```

### `<Map>.putAll(another map)`
- Description: This allows you to put all the keys and values of another map into this map
- Parameter - Map (`another map`): the map you want to merge into this map
- Example:
```kotlin
(map = {'key': 'value'}).putAll({'key2': 'value2'}); // map = {'key': 'value', 'key2': 'value2'}
```

### `<Map>.putIfAbsent(key, value)`
- Description: This allows you to put a key and value in the map if it doesn't exist
- Parameters:
  - Object (`key`): the key you want to put
  - Object (`value`): the value you want to put
- Example:
```kotlin
(map = {'key': 'value'}).putIfAbsent('key2', 'value2'); // map = {'key': 'value', 'key2': 'value2'}
```

### `<Map>.remove(key)`
- Description: This allows you to remove a key and its value from the map
- Parameter - Object (`key`): the key you want to remove
- Returns - Object: the value associated with the key, null if none
- Example:
```kotlin
{'key': 'value'}.remove('key'); // 'value'
```

## Static Methods

### `Map.unordered()`
- Description: This function allows you to create an unordered map
- Returns - Map: an unordered map
- Example:
```kotlin
Map.unordered();
```



# Material class
Material class for Arucas

This class represents all possible item and block types
and allows you to convert them into instances of ItemStacks and Blocks
Import with `import Material from Minecraft;`

## Methods

### `<Material>.asBlock()`
- Description: This converts the material into a Block.
If it cannot be converted an error will be thrown
- Returns - Block: the Block representation of the material
- Example:
```kotlin
material.asBlock();
```

### `<Material>.asItemStack()`
- Description: This converts the material into an ItemStack.
If it cannot be converted an error will be thrown
- Returns - ItemStack: the ItemStack representation of the material
- Example:
```kotlin
material.asItemStack();
```

### `<Material>.getFullId()`
- Description: This returns the full id of the material, for example: 'minecraft:diamond'
- Returns - String: the full id representation of the material
- Example:
```kotlin
material.getFullId();
```

### `<Material>.getId()`
- Description: This returns the id of the material, for example: 'diamond'
- Returns - String: the id representation of the material
- Example:
```kotlin
material.getId();
```

### `<Material>.getTranslatedName()`
- Description: This gets the translated name of the ItemStack, for example: 
Material.DIAMOND_SWORD would return 'Diamond Sword' if your language is English
- Returns - String: the translated name of the Material
- Example:
```kotlin
material.getTranslatedName();
```

## Static Methods

### `Material.of(id)`
- Description: This converts a block or item id into a Material.
This method will throw an error if the id is invalid
- Parameter - String (`id`): the id of the block or item
- Returns - Material: the material instance from the id
- Example:
```kotlin
Material.of('diamond');
```



# Math class
Math class for Arucas

Provides many basic math functions. This is a utility class, and cannot be constructed
Class does not need to be imported

## Static Fields

### `Math.e`
- Description: The value of e
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.e;
```
### `Math.pi`
- Description: The value of pi
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.pi;
```
### `Math.root2`
- Description: The value of root 2
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.root2;
```

## Static Methods

### `Math.abs(num)`
- Description: Returns the absolute value of a number
- Parameter - Number (`num`): the number to get the absolute value of
- Returns - Number: the absolute value of the number
- Example:
```kotlin
Math.abs(-3);
```

### `Math.arccos(num)`
- Description: Returns the arc cosine of a number
- Parameter - Number (`num`): the number to get the arc cosine of
- Returns - Number: the arc cosine of the number
- Example:
```kotlin
Math.arccos(Math.cos(Math.pi));
```

### `Math.arcsin(num)`
- Description: Returns the arc sine of a number
- Parameter - Number (`num`): the number to get the arc sine of
- Returns - Number: the arc sine of the number
- Example:
```kotlin
Math.arcsin(Math.sin(Math.pi));
```

### `Math.arctan(num)`
- Description: Returns the arc tangent of a number
- Parameter - Number (`num`): the number to get the arc tangent of
- Returns - Number: the arc tangent of the number
- Example:
```kotlin
Math.arctan(Math.tan(Math.pi));
```

### `Math.arctan2(y, x)`
- Description: Returns the angle theta of the polar coordinates (r, theta) that correspond to the rectangular
coordinates (x, y) by computing the arc tangent of the value y / x
- Parameters:
  - Number (`y`): the ordinate coordinate
  - Number (`x`): the abscissa coordinate
- Returns - Number: the theta component of the point (r, theta)
- Example:
```kotlin
Math.arctan2(Math.tan(Math.pi), Math.cos(Math.pi)); // -3.141592
```

### `Math.ceil(num)`
- Description: Rounds a number up to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.ceil(3.5);
```

### `Math.clamp(value, min, max)`
- Description: Clamps a value between a minimum and maximum
- Parameters:
  - Number (`value`): the value to clamp
  - Number (`min`): the minimum
  - Number (`max`): the maximum
- Returns - Number: the clamped value
- Example:
```kotlin
Math.clamp(10, 2, 8);
```

### `Math.cos(num)`
- Description: Returns the cosine of a number
- Parameter - Number (`num`): the number to get the cosine of
- Returns - Number: the cosine of the number
- Example:
```kotlin
Math.cos(Math.pi);
```

### `Math.cosec(num)`
- Description: Returns the cosecant of a number
- Parameter - Number (`num`): the number to get the cosecant of
- Returns - Number: the cosecant of the number
- Example:
```kotlin
Math.cosec(Math.pi);
```

### `Math.cosh(num)`
- Description: Returns the hyperbolic cosine of a number
- Parameter - Number (`num`): the number to get the hyperbolic cosine of
- Returns - Number: the hyperbolic cosine of the number
- Example:
```kotlin
Math.cosh(1);
```

### `Math.cot(num)`
- Description: Returns the cotangent of a number
- Parameter - Number (`num`): the number to get the cotangent of
- Returns - Number: the cotangent of the number
- Example:
```kotlin
Math.cot(Math.pi);
```

### `Math.floor(num)`
- Description: Rounds a number down to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.floor(3.5);
```

### `Math.lerp(start, end, delta)`
- Description: Linear interpolation between two numbers
- Parameters:
  - Number (`start`): the first number
  - Number (`end`): the second number
  - Number (`delta`): the interpolation factor
- Returns - Number: the interpolated number
- Example:
```kotlin
Math.lerp(0, 10, 0.5);
```

### `Math.ln(num)`
- Description: Returns the natural logarithm of a number
- Parameter - Number (`num`): the number to get the logarithm of
- Returns - Number: the natural logarithm of the number
- Example:
```kotlin
Math.ln(Math.e);
```

### `Math.log(base, num)`
- Description: Returns the logarithm of a number with a specified base
- Parameters:
  - Number (`base`): the base
  - Number (`num`): the number to get the logarithm of
- Returns - Number: the logarithm of the number
- Example:
```kotlin
Math.log(2, 4);
```

### `Math.log10(num)`
- Description: Returns the base 10 logarithm of a number
- Parameter - Number (`num`): the number to get the logarithm of
- Returns - Number: the base 10 logarithm of the number
- Example:
```kotlin
Math.log10(100);
```

### `Math.max(num1, num2)`
- Description: Returns the largest number
- Parameters:
  - Number (`num1`): the first number to compare
  - Number (`num2`): the second number to compare
- Returns - Number: the largest number
- Example:
```kotlin
Math.max(5, 2);
```

### `Math.min(num1, num2)`
- Description: Returns the smallest number
- Parameters:
  - Number (`num1`): the first number to compare
  - Number (`num2`): the second number to compare
- Returns - Number: the smallest number
- Example:
```kotlin
Math.min(5, 2);
```

### `Math.mod(num1, num2)`
- Description: Returns the modulus of a division
- Parameters:
  - Number (`num1`): the number to divide
  - Number (`num2`): the divisor
- Returns - Number: the modulus of the division
- Example:
```kotlin
Math.mod(5, 2);
```

### `Math.rem(num1, num2)`
- Description: Returns the remainder of a division
- Parameters:
  - Number (`num1`): the number to divide
  - Number (`num2`): the divisor
- Returns - Number: the remainder of the division
- Example:
```kotlin
Math.rem(5, 2);
```

### `Math.round(num)`
- Description: Rounds a number to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.round(3.5);
```

### `Math.sec(num)`
- Description: Returns the secant of a number
- Parameter - Number (`num`): the number to get the secant of
- Returns - Number: the secant of the number
- Example:
```kotlin
Math.sec(Math.pi);
```

### `Math.signum(num)`
- Description: Returns the sign of a number, 1 if the number is positive,
-1 if the number is negative, and 0 if the number is 0
- Parameter - Number (`num`): the number to get the sign of
- Returns - Number: the sign of the number
- Example:
```kotlin
Math.signum(3);
```

### `Math.sin(num)`
- Description: Returns the sine of a number
- Parameter - Number (`num`): the number to get the sine of
- Returns - Number: the sine of the number
- Example:
```kotlin
Math.sin(Math.pi);
```

### `Math.sinh(num)`
- Description: Returns the hyperbolic sine of a number
- Parameter - Number (`num`): the number to get the hyperbolic sine of
- Returns - Number: the hyperbolic sine of the number
- Example:
```kotlin
Math.sinh(1);
```

### `Math.sqrt(num)`
- Description: Returns the square root of a number
- Parameter - Number (`num`): the number to square root
- Returns - Number: the square root of the number
- Example:
```kotlin
Math.sqrt(9);
```

### `Math.tan(num)`
- Description: Returns the tangent of a number
- Parameter - Number (`num`): the number to get the tangent of
- Returns - Number: the tangent of the number
- Example:
```kotlin
Math.tan(Math.pi);
```

### `Math.tanh(num)`
- Description: Returns the hyperbolic tangent of a number
- Parameter - Number (`num`): the number to get the hyperbolic tangent of
- Returns - Number: the hyperbolic tangent of the number
- Example:
```kotlin
Math.tanh(1);
```

### `Math.toDegrees(num)`
- Description: Converts a number from radians to degrees
- Parameter - Number (`num`): the number to convert
- Returns - Number: the number in degrees
- Example:
```kotlin
Math.toDegrees(Math.pi);
```

### `Math.toRadians(num)`
- Description: Converts a number from degrees to radians
- Parameter - Number (`num`): the number to convert
- Returns - Number: the number in radians
- Example:
```kotlin
Math.toRadians(90);
```



# MerchantScreen class
MerchantScreen class for Arucas

This class extends Screen and so inherits all of their methods too,
this class is used to add functionality to trading screens.
Import with `import MerchantScreen from Minecraft;`

## Methods

### `<MerchantScreen>.clearTrade()`
- Description: This clears the currently selected trade.
You must be inside the merchant GUI or an error will be thrown
- Example:
```kotlin
screen.clearTrade();
```

### `<MerchantScreen>.doesVillagerHaveTrade(materialLike)`
- Description: This checks if the villager has a trade for a certain item.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Material (`materialLike`): the item or material to check for
- Returns - Boolean: true if the villager has a trade for the item, false otherwise
- Example:
```kotlin
screen.doesVillagerHaveTrade(Material.DIAMOND_PICKAXE);
```

### `<MerchantScreen>.getIndexOfTradeItem(material)`
- Description: This gets the index of a trade for a certain item.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Material (`material`): the item to get the index of
- Returns - Number: the index of the trade
- Example:
```kotlin
screen.getIndexOfTradeItem(Material.DIAMOND_PICKAXE);
```

### `<MerchantScreen>.getPriceForIndex(index)`
- Description: This gets the price of a trade at a certain index.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Number (`index`): the index of the trade
- Returns - Number: the price of the trade
- Example:
```kotlin
screen.getPriceForIndex(0);
```

### `<MerchantScreen>.getTradeItemForIndex(index)`
- Description: This gets the item stack of a trade at a certain index.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Number (`index`): the index of the trade
- Returns - ItemStack: the item stack of the trade
- Example:
```kotlin
screen.getTradeItemForIndex(0);
```

### `<MerchantScreen>.getTradeList()`
- Description: This gets a list of all the merchant's trades
- Returns - List: the list of all the Trades
- Example:
```kotlin
screen.getTradeList();
```

### `<MerchantScreen>.getTradeListSize()`
- Description: This gets the size of all the trades available
- Returns - Number: the size of the trade list
- Example:
```kotlin
screen.getTradeListSize();
```

### `<MerchantScreen>.getVillagerLevel()`
- Description: This gets the level of the villager, this will
throw an error if you are not trading with a villager.
The level can be between 1 - 5 from Novice to Master
- Returns - Number: the level of the villager
- Example:
```kotlin
screen.getVillagerLevel();
```

### `<MerchantScreen>.getVillagerXp()`
- Description: This gets the amount of xp in the villagers xp bar,
The total amount of xp is hardcoded for each level.
Level 2 requires 10 xp, 3 requires 70 (60 xp from 2 -> 3),
4 requires 150 (80 xp from 3 -> 4), 5 requires 250
(100 xp from 4 -> 5). 250 is the max xp a villager can have
- Returns - Number: the amount of xp
- Example:
```kotlin
screen.getVillagerXpBar
```

### `<MerchantScreen>.isTradeDisabled(index)`
- Description: This returns true if a trade is disabled at an index
- Parameter - Number (`index`): the index of the trade
- Returns - Boolean: true if a trade is disabled
- Example:
```kotlin
screen.isTradeDisabled(1);
```

### `<MerchantScreen>.isTradeSelected()`
- Description: This returns true if a trade is selected
- Returns - Boolean: true if a trade is selected
- Example:
```kotlin
screen.isTradeSelected();
```

### `<MerchantScreen>.selectTrade(index)`
- Description: This selects the currently selected trade, as if you were to click it.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Number (`index`): the index of the trade
- Example:
```kotlin
screen.selectTrade(0);
```

### `<MerchantScreen>.tradeIndex(index)`
- Description: This makes your player trade with the merchant at a certain index.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Number (`index`): the index of the trade
- Example:
```kotlin
screen.tradeIndex(0);
```

### `<MerchantScreen>.tradeSelected()`
- Description: This trades the currently selected trade.
You must be inside the merchant GUI or an error will be thrown
- Example:
```kotlin
screen.tradeSelected();
```

### `<MerchantScreen>.tradeSelectedAndThrow()`
- Description: This trades the currently selected trade and throws the items that were traded.
You must be inside the merchant GUI or an error will be thrown
- Example:
```kotlin
screen.tradeSelectedAndThrow();
```



# MinecraftClient class
MinecraftClient class for Arucas

This allows for many core interactions with the MinecraftClient
Import with `import MinecraftClient from Minecraft;`

## Methods

### `<MinecraftClient>.addCommand(command)`
- Description: This allows you to register your own client side command in game
- Parameter - Map (`command`): a command map or a command builder
- Example:
```kotlin
client.addCommand({
    "name": "example",
    "subcommands": { },
    "arguments": { }
});
```

### `<MinecraftClient>.canSendScriptPacket()`
- Description: Returns whether the server supports client script packets
- Returns - Boolean: Whether the client can send packets to the server
- Example:
```kotlin
client.canSendScriptPacket()
```

### `<MinecraftClient>.clearChat()`
- Description: This will clear the chat hud
- Example:
```kotlin
client.clearChat();
```

### `<MinecraftClient>.editSign(position, string...)`
- Description: This allows you to edit sign at certain position with given string(lines), up to 4 lines.
This function does not check if sign is editable / is in position.
- Parameters:
  - Pos (`position`): the position of sign
  - String (`string...`): the lines for the sign, requires 1 string and up to 4 strings
- Example:
```kotlin
client.editSign(new Pos(0,0,0), '100', '101', 'this is third line', 'last line');
```

### `<MinecraftClient>.getClientRenderDistance()`
- Description: This returns the current render distance set on the client
- Returns - Number: the render distance
- Example:
```kotlin
client.getClientRenderDistance();
```

### `<MinecraftClient>.getCursorStack()`
- Description: This returns the item stack that is currently being held by the cursor
- Returns - ItemStack: the item stack, will be Air if there is nothing
- Example:
```kotlin
client.getCursorStack();
```

### `<MinecraftClient>.getEssentialClientValue(ruleName)`
- Description: This gets the value of the given client rule.
This will throw an error if the rule name is invalid
- Parameter - String (`ruleName`): the client rule
- Returns - Object: the value of the client rule
- Example:
```kotlin
client.getEssentialClientValue('overrideCreativeWalkSpeed');
```

### `<MinecraftClient>.getFps()`
- Description: This gets the current fps
- Returns - Number: the fps
- Example:
```kotlin
client.getFps();
```

### `<MinecraftClient>.getLatestChatMessage()`
- Description: This will return the latest chat message
- Returns - Text: the latest chat message, null if there is none
- Example:
```kotlin
client.getLatestChatMessage();
```

### `<MinecraftClient>.getModList()`
- Description: This gets a list of all the mod ids of the mods installed
- Returns - List: the mod ids
- Example:
```kotlin
client.getModList();
```

### `<MinecraftClient>.getPing()`
- Description: This gets the current connected server's ping.
This will throw an error if the client is not connected to a server
- Returns - Number: the server ping in milliseconds
- Example:
```kotlin
client.getPing();
```

### `<MinecraftClient>.getPlayer()`
- Description: This returns the current player on the client
- Returns - Player: the main player
- Example:
```kotlin
client.getPlayer();
```

### `<MinecraftClient>.getRunDirectory()`
- Description: Returns the directory where the client is running
- Returns - File: the Minecraft run directory
- Example:
```kotlin
client.getRunDirectory();
```

### `<MinecraftClient>.getScriptsPath()`
- Description: This gets the script directory path, this is where all scripts are stored
- Returns - String: the script directory path
- Example:
```kotlin
client.getScriptPath();
```

### `<MinecraftClient>.getServerIp()`
- Description: This will return the server ip
- Returns - String: The server ip, null if in single player
- Example:
```kotlin
client.getServerIp();
```

### `<MinecraftClient>.getServerName()`
- Description: This gets the current connected server's name that you have set it to in the multiplayer screen
- Returns - String: the server name
- Example:
```kotlin
client.getServerName();
```

### `<MinecraftClient>.getVersion()`
- Description: This returns the current version of Minecraft you are playing
- Returns - String: the version for example: '1.17.1'
- Example:
```kotlin
client.getVersion();
```

### `<MinecraftClient>.getWorld()`
- Description: This returns the world that is currently being played on
- Returns - World: the world
- Example:
```kotlin
client.getWorld();
```

### `<MinecraftClient>.holdKey(key, milliseconds)`
- Description: This allows you to simulate a key being held inside of Minecraft, this will press, hold, and release.
This will throw an error if the given key is unknown
- Parameters:
  - String (`key`): the key to hold
  - Number (`milliseconds`): the number of milliseconds you want it held for
- Example:
```kotlin
client.holdKey('f', 100);
```

### `<MinecraftClient>.isInSinglePlayer()`
- Description: This will return true if the client is in single player mode
- Returns - Boolean: true if the client is in single player mode
- Example:
```kotlin
client.isInSinglePlayer();
```

### `<MinecraftClient>.parseStringToNbt(string)`
- Description: This parses a string and turns it into a Nbt compound
- Parameter - String (`string`): the string to parse
- Returns - Object: the nbt compound as a value
- Example:
```kotlin
client.parseStringToNbt('{"test":"test"}');
```

### `<MinecraftClient>.playSound(soundId, volume, pitch)`
- Description: This plays the given sound with the given volume and pitch around the player
sound id's can be found [here](https://minecraft.fandom.com/wiki/Sounds.json#Sound_events)
- Parameters:
  - String (`soundId`): the sound id you want to play
  - Number (`volume`): the volume of the sound
  - Number (`pitch`): the pitch of the sound
- Example:
```kotlin
client.playSound('entity.lightning_bolt.thunder', 1, 1);
```

### `<MinecraftClient>.playerNameFromUuid(uuid)`
- Description: This will return the player name from the given uuid
- Parameter - String (`uuid`): the uuid as a string
- Returns - String: the player name, null if the uuid is not found
- Example:
```kotlin
client.playerNameFromUuid('d4fca8c4-e083-4300-9a73-bf438847861c');
```

### `<MinecraftClient>.pressKey(key)`
- Description: This allows you to simulate a key press inside of Minecraft, this will only press the key down.
This will throw an error if the key is unknown
- Parameter - String (`key`): the key to press
- Example:
```kotlin
client.pressKey('f');
```

### `<MinecraftClient>.releaseKey(key)`
- Description: This allows you to simulate a key release inside of Minecraft, this
is useful for keys that only work on release, for example `F3`, this
will throw an error if the key is unknown
- Parameter - String (`key`): the key to release
- Example:
```kotlin
client.releaseKey('f');
```

### `<MinecraftClient>.renderFloatingItem(material)`
- Description: This renders an item in front of the player using the totem of undying animation
- Parameter - Material (`material`): the material to render
- Example:
```kotlin
client.renderFloatingItem(Material.DIAMOND);
```

### `<MinecraftClient>.resetEssentialClientRule(ruleName)`
- Description: This resets the given client rule to its default value.
This will throw an error if the rule name is invalid
- Parameter - String (`ruleName`): the client rule
- Example:
```kotlin
client.resetEssentialClientRule('highlightLavaSources');
```

### `<MinecraftClient>.run(function)`
- Description: This runs the given function on the main thread
- Parameter - Function (`function`): the function to run
- Example:
```kotlin
client.run(fun() { print('Do something'); });
```

### `<MinecraftClient>.runOnMainThread(function)`
- Deprecated: Use 'client.run(task)' instead
- Description: This runs the given function on the main thread
- Parameter - Function (`function`): the function to run
- Example:
```kotlin
client.runOnMainThread(fun() { print('Do something'); });
```

### `<MinecraftClient>.screenshot()`
- Description: This makes the client take a screenshot
- Example:
```kotlin
client.screenshot();
```

### `<MinecraftClient>.screenshot(name)`
- Description: This makes the client take a screenshot and saves it with a given name
- Parameter - String (`name`): the name of the file
- Example:
```kotlin
client.screenshot('screenshot.png');
```

### `<MinecraftClient>.sendScriptPacket(values...)`
- Description: This sends a script packet to the server
You can send the follow types of values:
Boolean, Number, String, List (of numbers), Text, ItemStack, Pos, and NbtMaps
You can send byte, int, and long arrays by using the strings 'b', 'i', and 'l' at the start of the list
- Parameter - Object (`values...`): the data you want to send to the server
- Example:
```kotlin
client.sendScriptPacket('test', false, ['l', 9999, 0, 45]);
```

### `<MinecraftClient>.setClientRenderDistance(number)`
- Description: This sets the render distance on the client
- Parameter - Number (`number`): the render distance
- Example:
```kotlin
client.setClientRenderDistance(10);
```

### `<MinecraftClient>.setCursorStack(itemStack)`
- Deprecated: You should use 'fakeInventoryScreen.setCursorStack(item)' instead
- Description: This sets the item stack that is currently being held by the cursor, this does not work
in normal screens only in FakeScreens, this does not actually pick up an item just display like you have
- Parameter - ItemStack (`itemStack`): the item stack to set
- Example:
```kotlin
client.setCursorStack(Material.DIAMOND.asItemStack());
```

### `<MinecraftClient>.setEssentialClientRule(ruleName, value)`
- Description: This sets the given client rule to the given value.
This may throw an error if the name is invalid or the rule cannot be set
- Parameters:
  - String (`ruleName`): the client rule
  - Object (`value`): the new value for the rule
- Example:
```kotlin
client.setEssentialClientRule('highlightLavaSources', false);
```

### `<MinecraftClient>.stripFormatting(string)`
- Description: This strips the formatting from the given string
- Parameter - String (`string`): the string to strip
- Returns - String: the stripped string
- Example:
```kotlin
client.stripFormatting('§cHello§r');
```

### `<MinecraftClient>.syncToTick()`
- Description: Synchronizes the current thread in Arucas to the next game tick
- Example:
```kotlin
client.syncToTick();
```

### `<MinecraftClient>.tick()`
- Description: This ticks the client
- Example:
```kotlin
client.tick();
```

### `<MinecraftClient>.uuidFromPlayerName(name)`
- Description: This will return the uuid from the given player name
- Parameter - String (`name`): the player name
- Returns - String: the uuid, null if the player name is not found
- Example:
```kotlin
client.uuidFromPlayerName('senseiwells');
```

## Static Methods

### `MinecraftClient.get()`
- Description: Returns the MinecraftClient instance
- Returns - MinecraftClient: the MinecraftClient instance
- Example:
```kotlin
MinecraftClient.get();
```

### `MinecraftClient.getClient()`
- Description: Returns the MinecraftClient instance
- Returns - MinecraftClient: the MinecraftClient instance
- Example:
```kotlin
MinecraftClient.getClient();
```



# MinecraftTask class
MinecraftTask class for Arucas

This class is used to create tasks that can be chained and
run on the main Minecraft thread. This ensures that all
behaviors work as intended.
Class does not need to be imported

## Constructors

### `new MinecraftTask()`
- Description: This creates a new empty Minecraft task
- Example:
```kotlin
task = new MinecraftTask();
```

## Methods

### `<MinecraftTask>.run()`
- Description: This runs the task on the main Minecraft thread. It returns a future
which can be awaited, the last function in the chain will be used as
the return value for the future
- Returns - Future: the future value that can be awaited
- Example:
```kotlin
task = new MinecraftTask()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .then(fun() 10);
f = task.run(); // prints 'hello world'
print(f.await()); // prints 10
```

### `<MinecraftTask>.waitThen(ticks, function)`
- Description: This adds a delay (in ticks) then runs the given task.
This delay is will also affect all following chained function
delays. If this is the last function in the chain, then the
return value will be determined by this function
- Parameters:
  - Number (`ticks`): the amount of ticks delay before the function runs
  - Function (`function`): the function to run after the delay
- Returns - MinecraftTask: the task, this allows for chaining
- Example:
```kotlin
task = new MinecraftTask()
    .then(fun() print("hello"))
    .waitThen(5, fun() print("world"));
task.run(); // prints 'hello', waits 5 ticks, prints 'world'
```



# Network class
Network class for Arucas

Allows you to do http requests. This is a utility class and cannot be constructed.
Import with `import Network from util.Network;`

## Static Methods

### `Network.downloadFile(url, file)`
- Description: Downloads a file from an url to a file
- Parameters:
  - String (`url`): the url to download from
  - File (`file`): the file to download to
- Returns - Boolean: whether the download was successful
- Example:
```kotlin
Network.downloadFile('https://arucas.com', new File('dir/downloads'));
```

### `Network.openUrl(url)`
- Description: Opens an url in the default browser
- Parameter - String (`url`): the url to open
- Example:
```kotlin
Network.openUrl('https://google.com');
```

### `Network.requestUrl(url)`
- Description: Requests an url and returns the response
- Parameter - String (`url`): the url to request
- Returns - String: the response from the url
- Example:
```kotlin
Network.requestUrl('https://google.com');
```



# Null class
Null class for Arucas

This class is used for the null object,
this cannot be instantiated or extended
Class does not need to be imported



# Number class
Number class for Arucas

This class cannot be constructed as it has a literal representation.
For math related functions see the Math class.
Class does not need to be imported

## Methods

### `<Number>.ceil()`
- Description: This allows you to round a number up to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.ceil();
```

### `<Number>.floor()`
- Description: This allows you to round a number down to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.floor();
```

### `<Number>.isInfinite()`
- Description: This allows you to check if a number is infinite
- Returns - Boolean: true if the number is infinite
- Example:
```kotlin
(1/0).isInfinite();
```

### `<Number>.isNaN()`
- Description: This allows you to check if a number is not a number
- Returns - Boolean: true if the number is not a number
- Example:
```kotlin
(0/0).isNaN();
```

### `<Number>.round()`
- Description: This allows you to round a number to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.round();
```



# Object class
Object class for Arucas

This is the base class for every other class in Arucas.
This class cannot be instantiated from, you can extend it
however every class already extends this class by default
Class does not need to be imported

## Methods

### `<Object>.copy()`
- Description: This returns a copy of the value if implemented.
Some objects that are immutable, such as Strings and Numbers
will not be copied, and will return the same instance.
Any object that has not implemented the copy method will also
return the same instance
- Returns - Object: a copy of the value
- Example:
```kotlin
[10, 11, 12].copy(); // [10, 11, 12]
```

### `<Object>.hashCode()`
- Description: This returns the hash code of the value, mainly used for maps and sets
the hash code of an object must remain consistent for objects to be able
to be used as keys in a map or set. If two objects are equal, they must
have the same hash code
- Returns - Number: the hash code of the value
- Example:
```kotlin
[10, 11, 12].hashCode(); // -1859087
```

### `<Object>.instanceOf(type)`
- Description: This returns true if the value is an instance of the given type
- Parameter - Type (`type`): the type to check against
- Returns - Boolean: true if the value is an instance of the given type
- Example:
```kotlin
[10, 11, 12].instanceOf(List.type); // true
```

### `<Object>.toString()`
- Description: This returns the string representation of the value
- Returns - String: the string representation of the value
- Example:
```kotlin
[10, 11, 12].toString(); // [10, 11, 12]
```

### `<Object>.uniqueHash()`
- Description: This returns the unique hash of the value, this is different for every instance of a value
- Returns - Number: the unique hash of the value
- Example:
```kotlin
'thing'.uniqueHash();
```



# OtherPlayer class
OtherPlayer class for Arucas

This class is used to represent all players, mainly other players,
this class extends LivingEntity and so inherits all of their methods too
Import with `import OtherPlayer from Minecraft;`

## Methods

### `<OtherPlayer>.getAbilities()`
- Description: This gets the abilities of the player in a map
For example:
`{"invulnerable": false, "canFly": true, "canBreakBlocks": true, "isCreative": true, "walkSpeed": 1.0, "flySpeed": 1.2}`
- Returns - Map: the abilities of the player
- Example:
```kotlin
otherPlayer.getAbilities();
```

### `<OtherPlayer>.getAllSlotsFor(materialLike)`
- Description: This gets all the slot numbers of the specified item in the players combined inventory
- Parameter - Material (`materialLike`): the item or material you want to get the slot of
- Returns - List: the slot numbers of the item, empty list if not found
- Example:
```kotlin
otherPlayer.getAllSlotsFor(Material.DIAMOND);
```

### `<OtherPlayer>.getAllSlotsFor(materialLike, inventoryType)`
- Description: This gets all the slot numbers of the specified item in the players combined inventory
- Parameters:
  - Material (`materialLike`): the item or material you want to get the slot of
  - String (`inventoryType`): all/combined -> includes external, player/main -> player slots, external/other -> excludes player inventory
- Returns - List: the slot numbers of the item, empty list if not found
- Example:
```kotlin
otherPlayer.getAllSlotsFor(Material.DIAMOND, 'player');
```

### `<OtherPlayer>.getCurrentSlot()`
- Description: This gets the players currently selected slot
- Returns - Number: the currently selected slot number
- Example:
```kotlin
otherPlayer.getCurrentSlot();
```

### `<OtherPlayer>.getFishingBobber()`
- Description: This gets the fishing bobber that the player has
- Returns - Entity: the fishing bobber entity, null if the player isn't fishing
- Example:
```kotlin
otherPlayer.getFishingBobber();
```

### `<OtherPlayer>.getGamemode()`
- Description: This gets the players gamemode, may be null if not known
- Returns - String: the players gamemode as a string, for example 'creative', 'survival', 'spectator'
- Example:
```kotlin
otherPlayer.getGamemode();
```

### `<OtherPlayer>.getHeldItem()`
- Description: This gets the players currently selected item, in their main hand
- Returns - ItemStack: the currently selected item
- Example:
```kotlin
otherPlayer.getHeldItem();
```

### `<OtherPlayer>.getHunger()`
- Description: This gets the hunger level of the player
- Returns - Number: the hunger level
- Example:
```kotlin
otherPlayer.getHunger();
```

### `<OtherPlayer>.getItemForPlayerSlot(slotNum)`
- Description: This gets the item in the specified slot, in the players inventory, not including inventories of open containers.
This will throw an error if the slot is out of bounds
- Parameter - Number (`slotNum`): the slot number you want to get
- Returns - ItemStack: the item in the specified slot
- Example:
```kotlin
otherPlayer.getItemForPlayerSlot(0);
```

### `<OtherPlayer>.getItemForSlot(slotNum)`
- Description: This gets the item in the specified slot, in the total players inventory, including inventories of open containers.
This will throw an error if the index is out of bounds
- Parameter - Number (`slotNum`): the slot number you want to get
- Returns - ItemStack: the item in the specified slot
- Example:
```kotlin
otherPlayer.getItemForSlot(0);
```

### `<OtherPlayer>.getLevels()`
- Description: This gets the number of experience levels the player has
- Returns - Number: the number of experience levels
- Example:
```kotlin
otherPlayer.getLevels();
```

### `<OtherPlayer>.getNextLevelExperience()`
- Description: This gets the number of experience required to level up for the player
- Returns - Number: the number required to next level
- Example:
```kotlin
otherPlayer.getNextLevelExperience();
```

### `<OtherPlayer>.getPlayerName()`
- Description: This gets the players name
- Returns - String: the players name
- Example:
```kotlin
otherPlayer.getPlayerName();
```

### `<OtherPlayer>.getSaturation()`
- Description: This gets the saturation level of the player
- Returns - Number: the saturation level
- Example:
```kotlin
otherPlayer.getSaturation();
```

### `<OtherPlayer>.getSlotFor(materialLike)`
- Description: This gets the slot number of the specified item in the players combined inventory
- Parameter - Material (`materialLike`): the item or material you want to get the slot of
- Returns - Number: the slot number of the item, null if not found
- Example:
```kotlin
otherPlayer.getSlotFor(Material.DIAMOND.asItemStack());
```

### `<OtherPlayer>.getTotalSlots()`
- Description: This gets the players total inventory slots
- Returns - Number: the players total inventory slots
- Example:
```kotlin
otherPlayer.getTotalSlots();
```

### `<OtherPlayer>.getXpProgress()`
- Description: This gets the number of experience progress the player has
- Returns - Number: the number of experience progress
- Example:
```kotlin
otherPlayer.getXpProgress();
```

### `<OtherPlayer>.isInventoryFull()`
- Description: This gets whether the players inventory is full
- Returns - Boolean: whether the inventory is full
- Example:
```kotlin
otherPlayer.isInventoryFull();
```

### `<OtherPlayer>.isPlayerSlot(slotNum)`
- Description: This gets inventory type (player / other) for given slot numbers.
This will throw an error if the index is out of bounds
- Parameter - Number (`slotNum`): the slot number you want to get
- Returns - Boolean: whether slot was player inventory or not
- Example:
```kotlin
otherPlayer.isPlayerSlot(0);
```



# OutlinedShape class
OutlinedShape class for Arucas

This class represents all shapes that can be outlined
Class does not need to be imported

## Methods

### `<OutlinedShape>.getOutlineBlue()`
- Description: This gets the outline blue value of the shape
- Returns - Number: the blue value of the outline
- Example:
```kotlin
shape.getOutlineBlue();
```

### `<OutlinedShape>.getOutlineGreen()`
- Description: This gets the outline green value of the shape
- Returns - Number: the green value of the outline
- Example:
```kotlin
shape.getOutlineGreen();
```

### `<OutlinedShape>.getOutlineRed()`
- Description: This gets the outline red value of the shape
- Returns - Number: the red value of the outline
- Example:
```kotlin
shape.getOutlineRed();
```

### `<OutlinedShape>.getOutlineWidth()`
- Description: This gets the outline width of the shape
- Returns - Number: the width of the outline
- Example:
```kotlin
shape.getOutlineWidth();
```

### `<OutlinedShape>.setOutlineBlue(blue)`
- Description: This sets the outline blue value of the shape, using a single value
- Parameter - Number (`blue`): the amount of blue between 0 - 255
- Example:
```kotlin
shape.setOutlineBlue(34);
```

### `<OutlinedShape>.setOutlineColour(colour)`
- Description: This sets the width of the shape, using a single value, this function
also has a sibling named `setOutlineColor()` that has the same functionality.
The colour generally should be hexadecimal in the form 0xRRGGBB
- Parameter - Number (`colour`): the colour you want to set
- Example:
```kotlin
shape.setOutlineColour(0xFF00FF);
```

### `<OutlinedShape>.setOutlineColour(red, green, blue)`
- Description: This sets the outline colour of the shape, using three values, this function
also has a sibling named `setOutlineColor()` that has the same functionality.
If the colours are not between 0 and 255 an error will be thrown
- Parameters:
  - Number (`red`): the amount of red 0 - 255
  - Number (`green`): the amount of green 0 - 255
  - Number (`blue`): the amount of blue 0 - 255
- Example:
```kotlin
shape.setOutlineColour(255, 0, 255);
```

### `<OutlinedShape>.setOutlineGreen(green)`
- Description: This sets the outline green value of the shape, using a single value
- Parameter - Number (`green`): the amount of green between 0 - 255
- Example:
```kotlin
shape.setOutlineGreen(34);
```

### `<OutlinedShape>.setOutlineRed(red)`
- Description: This sets the outline red value of the shape, using a single value
- Parameter - Number (`red`): the amount of red between 0 - 255
- Example:
```kotlin
shape.setOutlineRed(34);
```

### `<OutlinedShape>.setOutlineWidth(width)`
- Description: This sets the outline width of the shape, this should not be negative
- Parameter - Number (`width`): the width of the outline
- Example:
```kotlin
shape.setOutlineWidth(2);
```



# Player class
Player class for Arucas

This class is used to interact with the main player, this extends OtherPlayer
and so inherits all methods from that class.
Import with `import Player from Minecraft;`

## Methods

### `<Player>.anvil(predicate1, predicate2)`
- Description: This allows you to combine two items in an anvil
- Parameters:
  - Function (`predicate1`): a function determining whether the first ItemStack meets a criteria
  - Function (`predicate2`): a function determining whether the second ItemStack meets a criteria
- Returns - Future: whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost
- Example:
```kotlin
// Enchant a pickaxe with mending
player.anvil(
    // Predicate for pick
    fun(item) {
        // We want a netherite pickaxe without mending
        if (item.getItemId() == "netherite_pickaxe") {
            hasMending = item.getEnchantments().getKeys().contains("mending");
            return !hasMending;
        }
        return false;
    },
    // Predicate for book
    fun(item) {
        // We want a book with mending
        if (item.getItemId() == "enchanted_book") {
            hasMending = item.getEnchantments().getKeys().contains("mending");
            return hasMending;
        }
        return false;
    }
);
```

### `<Player>.anvil(predicate1, predicate2, take)`
- Description: This allows you to combine two items in an anvil
- Parameters:
  - Function (`predicate1`): a function determining whether the first ItemStack meets a criteria
  - Function (`predicate2`): a function determining whether the second ItemStack meets a criteria
  - Boolean (`take`): whether you should take the item after putting items in the anvil
- Returns - Future: whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost
- Example:
```kotlin
// Enchant a pickaxe with mending
player.anvil(
    // Predicate for pick
    fun(item) {
        // We want a netherite pickaxe without mending
        if (item.getItemId() == "netherite_pickaxe") {
            hasMending = item.getEnchantments().getKeys().contains("mending");
            return !hasMending;
        }
        return false;
    },
    // Predicate for book
    fun(item) {
        // We want a book with mending
        if (item.getItemId() == "enchanted_book") {
            hasMending = item.getEnchantments().getKeys().contains("mending");
            return hasMending;
        }
        return false;
    },
    false
);
```

### `<Player>.anvilRename(name, predicate)`
- Description: This allows you to name an item in an anvil
- Parameters:
  - String (`name`): the name you want to give the item
  - Function (`predicate`): whether the ItemStack meets a certain criteria
- Returns - Future: whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost
- Example:
```kotlin
// Rename any shulker box
player.anvilRename("Rocket Box",
    fun(item) {
        isShulker = item.getItemId().containsString("shulker_box"));
        return isShulker;
    }
);
```

### `<Player>.attack(action)`
- Description: This allows you to make your player attack, you must
pass 'hold', 'stop', or 'once' otherwise an error will be thrown
- Parameter - String (`action`): the type of action, either 'hold', 'stop', or 'once'
- Example:
```kotlin
player.attack('once');
```

### `<Player>.attackBlock(pos, direction)`
- Description: This allows you to attack a block at a position and direction
- Parameters:
  - Pos (`pos`): the position of the block
  - String (`direction`): the direction of the attack, e.g. 'up', 'north', 'east', etc.
- Example:
```kotlin
player.attackBlock(new Pos(0, 0, 0), 'up');
```

### `<Player>.attackBlock(x, y, z, direction)`
- Description: This allows you to attack a block at a position and direction
- Parameters:
  - Number (`x`): the x position
  - Number (`y`): the y position
  - Number (`z`): the z position
  - String (`direction`): the direction of the attack, e.g. 'up', 'north', 'east', etc.
- Example:
```kotlin
player.attackBlock(0, 0, 0, 'up');
```

### `<Player>.attackEntity(entity)`
- Description: This makes your player attack an entity without
having to be looking at it or clicking on the entity
- Parameter - Entity (`entity`): the entity to attack
- Example:
```kotlin
allEntities = client.getWorld().getAllEntities();
foreach (entity : allEntities) {
    if (entity.getId() == "villager" && player.getSquaredDistanceTo(entity) < 5) {
        player.attackEntity(entity);
        break;
    }
}
```

### `<Player>.breakBlock(pos)`
- Description: This breaks a block at a given position, if it is able to be broken
- Parameter - Pos (`pos`): the position of the block
- Returns - Future: the future will be completed when the block is broken
- Example:
```kotlin
player.breakBlock(new Pos(0, 0, 0));
```

### `<Player>.canPlaceBlockAt(pos)`
- Description: Checks block can be placed at given position
- Parameter - Pos (`pos`): the position to check
- Example:
```kotlin
player.canPlaceBlockAt(block, pos);
```

### `<Player>.canPlaceBlockAt(block, x, y, z)`
- Description: Checks block can be placed at given position
- Parameters:
  - Block (`block`): the block to check for
  - Number (`x`): the x coordinate of the position
  - Number (`y`): the y coordinate of the position
  - Number (`z`): the z coordinate of the position
- Example:
```kotlin
player.canPlaceBlockAt(block, 0, 0, 0);
```

### `<Player>.clickCreativeStack(itemStack, slot)`
- Description: This allows you to click Creative stack, but requires sync with server
- Parameters:
  - ItemStack (`itemStack`): Stack to click
  - Number (`slot`): the slot to click
- Example:
```kotlin
player.clickCreativeStack(Material.DIAMOND_SWORD.asItemStack(), 9);
```

### `<Player>.clickRecipe(recipe)`
- Description: This allows you to click a predefined recipe
- Parameter - Recipe (`recipe`): the recipe you want to select
- Example:
```kotlin
player.clickRecipe(Recipe.CHEST);
```

### `<Player>.clickRecipe(recipe, boolean)`
- Description: This allows you to click a predefined recipe
- Parameters:
  - Recipe (`recipe`): the recipe you want to select
  - Boolean (`boolean`): whether to shift click the recipe
- Example:
```kotlin
player.clickRecipe(Recipe.CHEST, true);
```

### `<Player>.clickSlot(slot, click, action)`
- Description: This allows you to click a slot with either right or left click
and a slot action, the click must be either 'left' or 'right' or a number (for swap).
The action must be either 'click', 'shift_click', 'swap', 'middle_click',
'throw', 'drag', or 'double_click' or an error will be thrown
- Parameters:
  - Number (`slot`): the slot to click
  - String (`click`): the click type, this should be either 'left' or 'right'
  - String (`action`): the action to perform
- Example:
```kotlin
player.clickSlot(9, 'left', 'double_click');
```

### `<Player>.closeScreen()`
- Description: This closes the current screen
- Example:
```kotlin
player.closeScreen();
```

### `<Player>.craft(recipe)`
- Description: This allows you to craft a recipe, this can be 2x2 or 3x3
The list you pass in must contain Materials or ItemStacks
Most of the time you should use craftRecipe instead. You must
be in an appropriate gui for the crafting recipe or an error will be thrown
- Parameter - List (`recipe`): a list of materials making up the recipe you want to craft including air
- Example:
```kotlin
chestRecipe = [
    Material.OAK_PLANKS, Material.OAK_PLANKS, Material.OAK_PLANKS,
    Material.OAK_PLANKS,    Material.AIR    , Material.OAK_PLANKS,
    Material.OAK_PLANKS, Material.OAK_PLANKS, Material.OAK_PLANKS
];
player.craft(chestRecipe);
```

### `<Player>.craftRecipe(recipe)`
- Description: This allows you to craft a predefined recipe
- Parameter - Recipe (`recipe`): the recipe you want to craft
- Example:
```kotlin
player.craftRecipe(Recipe.CHEST);
```

### `<Player>.craftRecipe(recipe, boolean)`
- Description: This allows you to craft a predefined recipe
- Parameters:
  - Recipe (`recipe`): the recipe you want to craft
  - Boolean (`boolean`): whether result should be dropped or not
- Example:
```kotlin
player.craftRecipe(Recipe.CHEST, true);
```

### `<Player>.dropAll(material)`
- Description: This drops all items of a given type in the player's inventory
- Parameter - Material (`material`): the item stack, or material type to drop
- Example:
```kotlin
player.dropAll(Material.DIRT.asItemStack());
```

### `<Player>.dropAllExact(itemStack)`
- Description: This drops all the items that have the same nbt as a given stack
- Parameter - ItemStack (`itemStack`): the stack with nbt to drop
- Example:
```kotlin
player.dropAllExact(Material.GOLD_INGOT.asItemStack());
```

### `<Player>.dropItemInHand(dropAll)`
- Description: This drops the item(s) in the player's main hand
- Parameter - Boolean (`dropAll`): if true, all items in the player's main hand will be dropped
- Example:
```kotlin
player.dropItemInHand(true);
```

### `<Player>.dropSlot(slot)`
- Description: This allows you to drop the items in a slot
- Parameter - Number (`slot`): the slot to drop
- Example:
```kotlin
player.dropSlot(9);
```

### `<Player>.fakeLook(yaw, pitch, direction, duration)`
- Description: This makes the player 'fake' looking in a direction, this can be
used to place blocks in unusual orientations without moving the camera
- Parameters:
  - Number (`yaw`): the yaw to look at
  - Number (`pitch`): the pitch to look at
  - String (`direction`): the direction to look at
  - Number (`duration`): the duration of the look in ticks
- Example:
```kotlin
player.fakeLook(90, 0, 'up', 100);
```

### `<Player>.getBlockBreakingSpeed(itemStack, block)`
- Description: This returns the block breaking speed of the player on a block including enchanements and effects
- Parameters:
  - ItemStack (`itemStack`): item to test with
  - Block (`block`): the block to get the speed of
- Example:
```kotlin
speed = player.getBlockBreakingSpeed(Material.NETHERITE_PICKAXE.asItem(), Material.GOLD_BLOCK.asBlock());
```

### `<Player>.getCurrentScreen()`
- Description: This gets the current screen the player is in
- Returns - Screen: the screen the player is in, if the player is not in a screen it will return null
- Example:
```kotlin
screen = player.getCurrentScreen();
```

### `<Player>.getLookingAtEntity()`
- Description: This gets the entity that the player is currently looking at
- Returns - Entity: the entity that the player is looking at
- Example:
```kotlin
player.getLookingAtEntity();
```

### `<Player>.getSwappableHotbarSlot()`
- Description: This will get the next empty slot in the hotbar starting from the current slot
going right, and if it reaches the end of the hotbar it will start from the beginning.
If there is no empty slot it will return any slot that doesn't have an item with
an enchantment that is in the hotbar, again going from the current slot
if there is no such slot it will return the current selected slot
- Returns - Number: the slot that is swappable
- Example:
```kotlin
player.getSwappableHotbarSlot();
```

### `<Player>.interactBlock(pos, direction)`
- Description: This allows you to interact with a block at a position and direction
- Parameters:
  - Pos (`pos`): the position of the block
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
- Example:
```kotlin
player.interactBlock(new Pos(0, 0, 0), 'up');
```

### `<Player>.interactBlock(pos, direction, hand)`
- Description: This allows you to interact with a block at a position, direction, and hand
- Parameters:
  - Pos (`pos`): the position of the block
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
  - String (`hand`): the hand to use, e.g. 'main_hand', 'off_hand'
- Example:
```kotlin
player.interactBlock(new Pos(0, 0, 0), 'up', 'off_hand');
```

### `<Player>.interactBlock(x, y, z, direction)`
- Description: This allows you to interact with a block at a position and direction
- Parameters:
  - Number (`x`): the x position
  - Number (`y`): the y position
  - Number (`z`): the z position
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
- Example:
```kotlin
player.interactBlock(0, 100, 0, 'up');
```

### `<Player>.interactBlock(pos, direction, blockPos, insideBlock)`
- Description: This allows you to interact with a block at a position and direction
This function is for very specific cases where there needs to be extra precision
like when placing stairs or slabs in certain directions, so the first set of
coords is the exact position of the block, and the second set of coords is the position
- Parameters:
  - Pos (`pos`): the exact position of the block
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
  - Pos (`blockPos`): the position of the block
  - Boolean (`insideBlock`): whether the player is inside the block
- Example:
```kotlin
player.interactBlock(new Pos(0, 15.5, 0), 'up', new Pos(0, 15, 0), true);
```

### `<Player>.interactBlock(pos, direction, hand, blockPos, insideBlock)`
- Description: This allows you to interact with a block at a position and direction
This function is for very specific cases where there needs to be extra precision
like when placing stairs or slabs in certain directions, so the first set of
coords is the exact position of the block, and the second set of coords is the position
- Parameters:
  - Pos (`pos`): the exact position of the block
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
  - String (`hand`): the hand to use, e.g. 'main_hand', 'off_hand'
  - Pos (`blockPos`): the position of the block
  - Boolean (`insideBlock`): whether the player is inside the block
- Example:
```kotlin
player.interactBlock(new Pos(0, 15.5, 0), 'up', new Pos(0, 15, 0), true, 'off_hand');
```

### `<Player>.interactBlock(x, y, z, direction, blockX, blockY, blockZ, insideBlock)`
- Description: This allows you to interact with a block at a position and direction
This function is for very specific cases where there needs to be extra precision
like when placing stairs or slabs in certain directions, so the first set of
coords is the exact position of the block, and the second set of coords is the position
- Parameters:
  - Number (`x`): the exact x position
  - Number (`y`): the exact y position
  - Number (`z`): the exact z position
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
  - Number (`blockX`): the x position of the block
  - Number (`blockY`): the y position of the block
  - Number (`blockZ`): the z position of the block
  - Boolean (`insideBlock`): whether the player is inside the block
- Example:
```kotlin
player.interactBlock(0, 100.5, 0, 'up', 0, 100, 0, true);
```

### `<Player>.interactItem(hand)`
- Description: This allows you to interact item with given Hand
- Parameter - String (`hand`):  Hand to use, either 'main' or 'offhand'
- Example:
```kotlin
player.interactItem('main');
```

### `<Player>.interactWithEntity(entity)`
- Description: This allows your player to interact with an entity without
having to be looking at it or clicking on the entity
- Parameter - Entity (`entity`): the entity to interact with
- Example:
```kotlin
allEntities = client.getWorld().getAllEntities();
foreach (entity : allEntities) {
    if (entity.getId() == "villager" && player.getSquaredDistanceTo(entity) < 5) {
        player.interactWithEntity(entity);
        break;
    }
}
```

### `<Player>.jump()`
- Description: This will make the player jump if they are on the ground
- Example:
```kotlin
player.jump();
```

### `<Player>.logout(message)`
- Description: This forces the player to leave the world
- Parameter - String (`message`): the message to display to the player on the logout screen
- Example:
```kotlin
player.logout('You've been lazy!');
```

### `<Player>.look(yaw, pitch)`
- Description: This sets the player's look direction
- Parameters:
  - Number (`yaw`): the yaw of the player's look direction
  - Number (`pitch`): the pitch of the player's look direction
- Example:
```kotlin
player.look(0, 0);
```

### `<Player>.lookAtPos(pos)`
- Description: This makes your player look towards a position
- Parameter - Pos (`pos`): the position to look at
- Example:
```kotlin
player.lookAtPos(pos);
```

### `<Player>.lookAtPos(x, y, z)`
- Description: This makes your player look towards a position
- Parameters:
  - Number (`x`): the x coordinate of the position
  - Number (`y`): the y coordinate of the position
  - Number (`z`): the z coordinate of the position
- Example:
```kotlin
player.lookAtPos(0, 0, 0);
```

### `<Player>.message(message)`
- Description: This allows you to send a message to your player, only they will see this, purely client side
- Parameter - Text (`message`): the message to send, can also be string
- Example:
```kotlin
player.message('Hello World!');
```

### `<Player>.messageActionBar(message)`
- Description: This allows you to set the current memssage displaying on the action bar
- Parameter - Text (`message`): the message to send, can also be string
- Example:
```kotlin
player.messageActionBar('Hello World!');
```

### `<Player>.openInventory()`
- Description: This opens the player's inventory
- Example:
```kotlin
player.openInventory();
```

### `<Player>.openScreen(screen)`
- Description: This opens a screen for the player, this cannot open server side screens.
This will throw an error if you are trying to open a handled screen
- Parameter - Screen (`screen`): the screen to open
- Example:
```kotlin
player.openScreen(new FakeScreen('MyScreen', 4));
```

### `<Player>.say(message)`
- Description: This allows you to make your player send a message in chat, this includes commands
- Parameter - String (`message`): the message to send
- Example:
```kotlin
player.say('/help');
```

### `<Player>.setSelectedSlot(slot)`
- Description: This allows you to set the slot number your player is holding.
If the number is not between 0 and 8 an error will be thrown
- Parameter - Number (`slot`): the slot number, must be between 0 - 8
- Example:
```kotlin
player.setSelectedSlot(0);
```

### `<Player>.setSneaking(sneaking)`
- Description: This sets the player's sneaking state
- Parameter - Boolean (`sneaking`): the sneaking state
- Example:
```kotlin
player.setSneaking(true);
```

### `<Player>.setSprinting(sprinting)`
- Description: This sets the player's sprinting state
- Parameter - Boolean (`sprinting`): the sprinting state
- Example:
```kotlin
player.setSprinting(true);
```

### `<Player>.setWalking(walking)`
- Description: This sets the player's walking state
- Parameter - Boolean (`walking`): the walking state
- Example:
```kotlin
player.setWalking(true);
```

### `<Player>.shiftClickSlot(slot)`
- Description: This allows you to shift click a slot
- Parameter - Number (`slot`): the slot to click
- Example:
```kotlin
player.shiftClickSlot(9);
```

### `<Player>.showTitle(title, subtitle)`
- Description: THis allows you to show a title and subtitle to the player
- Parameters:
  - Text (`title`): the title to show, can be string or null
  - Text (`subtitle`): the subtitle to show, can be string or null
- Example:
```kotlin
player.showTitle('Title!', 'Subtitle!');
```

### `<Player>.spectatorTeleport(entity)`
- Description: This allows you to teleport to any entity as long as you are in spectator mode
- Parameter - Entity (`entity`): the entity to teleport to
- Example:
```kotlin
player.spectatorTeleport(player.getLookingAtEntity());
```

### `<Player>.stonecutter(itemInput, itemOutput)`
- Description: This allows you to use the stonecutter
- Parameters:
  - Material (`itemInput`): the item or material you want to input
  - Material (`itemOutput`): the item or material you want to craft
- Returns - Future: whether the result was successful
- Example:
```kotlin
player.stonecutter(Material.STONE.asItemstack(), Material.STONE_BRICKS.asItemStack());
```

### `<Player>.swapHands()`
- Description: This will swap the player's main hand with the off hand
- Example:
```kotlin
player.swapHands();
```

### `<Player>.swapPlayerSlotWithHotbar(slot)`
- Description: This allows you to swap a slot in the player's inventory with the hotbar
- Parameter - Number (`slot`): the slot to swap
- Example:
```kotlin
player.swapPlayerSlotWithHotbar(15);
```

### `<Player>.swapSlots(slot1, slot2)`
- Description: The allows you to swap two slots with one another.
A note about slot order is that slots go from top to bottom.
This will throw an errof if the slots are out of bounds
- Parameters:
  - Number (`slot1`): the slot to swap with slot2
  - Number (`slot2`): the slot to swap with slot1
- Example:
```kotlin
player.swapSlots(13, 14);
```

### `<Player>.swingHand(hand)`
- Description: This will play the player's hand swing animation for a given hand
- Parameter - String (`hand`): the hand to swing, this should be either 'main_hand' or 'off_hand'
- Example:
```kotlin
player.swingHand('main_hand');
```

### `<Player>.updateBreakingBlock(pos)`
- Deprecated: Consider using other alternatives for breaking blocks, e.g. <Player>.breakBlock
- Description: This allows you to update your block breaking progress at a position
- Parameter - Pos (`pos`): the position of the block
- Example:
```kotlin
player.updateBreakingBlock(new Pos(0, 0, 0));
```

### `<Player>.updateBreakingBlock(x, y, z)`
- Deprecated: Consider using other alternatives for breaking blocks, e.g. <Player>.breakBlock
- Description: This allows you to update your block breaking progress at a position
- Parameters:
  - Number (`x`): the x position
  - Number (`y`): the y position
  - Number (`z`): the z position
- Example:
```kotlin
player.updateBreakingBlock(0, 0, 0);
```

### `<Player>.use(action)`
- Description: This allows you to make your player use, you must
pass 'hold', 'stop', or 'once' otherwise an error will be thrown
- Parameter - String (`action`): the type of action, either 'hold', 'stop', or 'once'
- Example:
```kotlin
player.use('hold');
```

## Static Methods

### `Player.get()`
- Description: This gets the main player
- Returns - Player: The main player
- Example:
```kotlin
player = Player.get();
```



# Pos class
Pos class for Arucas

This class is a wrapper for 3 coordinate points in Minecraft
Import with `import Pos from Minecraft;`

## Constructors

### `new Pos(list)`
- Description: Creates a new Pos object with the given coordinates in a list
- Parameter - List (`list`): the list containing three coordinates
- Example:
```kotlin
new Pos([1, 2, 3])
```

### `new Pos(x, y, z)`
- Description: This creates a new Pos with the given x, y, and z
- Parameters:
  - Number (`x`): the x position
  - Number (`y`): the y position
  - Number (`z`): the z position
- Example:
```kotlin
new Pos(100, 0, 96);
```

## Methods

### `<Pos>.add(pos)`
- Description: This returns a new Pos with the current pos x, y, and z added by the given pos x, y, and z
- Parameter - Pos (`pos`): the Pos to add by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.add(new Pos(2, 3, 5));
```

### `<Pos>.add(x, y, z)`
- Description: This returns a new Pos with the current pos x, y, and z added by the given x, y, and z
- Parameters:
  - Number (`x`): the x adder
  - Number (`y`): the y adder
  - Number (`z`): the z adder
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.add(2, 3, 5);
```

### `<Pos>.asCentre()`
- Description: This returns center value of the position
- Returns - Pos: the center of the position
- Example:
```kotlin
pos.asCentre();
```

### `<Pos>.crossProduct(pos)`
- Description: This returns the cross product of the current pos and the given pos
- Parameter - Pos (`pos`): the Pos to cross product with
- Returns - Pos: the cross product
- Example:
```kotlin
pos.crossProduct(new Pos(2, 3, 5));
```

### `<Pos>.distanceTo(other)`
- Description: This returns distance to other position
- Parameter - Pos (`other`): other position
- Returns - Number: distance to other position
- Example:
```kotlin
pos.distanceTo(new Pos(0, 0, 0));
```

### `<Pos>.distanceTo(x, y, z)`
- Description: This returns distance to other x, y, z position
- Parameters:
  - Number (`x`): other position x
  - Number (`y`): other position y
  - Number (`z`): other position z
- Returns - Number: distance to other position
- Example:
```kotlin
pos.distanceTo(0, 0, 0);
```

### `<Pos>.dotProduct(pos)`
- Description: This returns the dot product of the current pos and the given pos
- Parameter - Pos (`pos`): the Pos to dot product with
- Returns - Number: the dot product
- Example:
```kotlin
pos.dotProduct(new Pos(2, 3, 5));
```

### `<Pos>.down()`
- Description: This returns a new Pos with the current pos y decremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.down();
```

### `<Pos>.down(number)`
- Description: This returns a new Pos with the current pos y decremented by the given number
- Parameter - Number (`number`): the number to decrement by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.down(2);
```

### `<Pos>.east()`
- Description: This returns a new Pos with the current pos x incremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.east();
```

### `<Pos>.east(number)`
- Description: This returns a new Pos with the current pos x incremented by the given number
- Parameter - Number (`number`): the number to increment by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.east(2);
```

### `<Pos>.getSidePos(direction)`
- Description: This returns side position value of position
- Parameter - String (`direction`): the direction, can be: north, south, east, west, up, down
- Returns - Pos: the side of the position
- Example:
```kotlin
pos.getSidePos('east');
```

### `<Pos>.getX()`
- Description: This returns the x position of the Pos
- Returns - Number: the x position
- Example:
```kotlin
pos.getX();
```

### `<Pos>.getY()`
- Description: This returns the y position of the Pos
- Returns - Number: the y position
- Example:
```kotlin
pos.getY();
```

### `<Pos>.getZ()`
- Description: This returns the z position of the Pos
- Returns - Number: the z position
- Example:
```kotlin
pos.getZ();
```

### `<Pos>.isNear(entity)`
- Description: This returns whether position to entity is less than 4.5
- Parameter - Entity (`entity`): the entity you want to check
- Returns - Boolean: whether entity is within 4.5 block distance
- Example:
```kotlin
pos.isNear(Player.get());
```

### `<Pos>.isWithin(entity, distance)`
- Description: This returns whether position to entity is less than given distance
- Parameters:
  - Entity (`entity`): the entity you want to check
  - Number (`distance`): the distance you want to check
- Returns - Boolean: whether entity is within given distance
- Example:
```kotlin
pos.isNear(player, 8);
```

### `<Pos>.multiply(pos)`
- Description: This returns a new Pos with the current pos x, y, and z multiplied by the given pos x, y, and z
- Parameter - Pos (`pos`): the Pos to multiply by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.multiply(new Pos(2, 3, 5));
```

### `<Pos>.multiply(x, y, z)`
- Description: This returns a new Pos with the current pos x, y, and z multiplied by the given x, y, and z
- Parameters:
  - Number (`x`): the x multiplier
  - Number (`y`): the y multiplier
  - Number (`z`): the z multiplier
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.multiply(2, 3, 5);
```

### `<Pos>.normalize()`
- Description: Normalizes the vector to have a magnitude of 1
- Returns - Pos: the normalized position
- Example:
```kotlin
pos.normalize();
```

### `<Pos>.north()`
- Description: This returns a new Pos with the current pos z incremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.north();
```

### `<Pos>.north(number)`
- Description: This returns a new Pos with the current pos z incremented by the given number
- Parameter - Number (`number`): the number to increment by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.north(2);
```

### `<Pos>.offset(direction)`
- Description: This returns a new Pos with the current pos x, y, and z offset by a direction
- Parameter - String (`direction`): the direction to offset by, must be one of: north, south, east, west, up, down
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.offset('north');
```

### `<Pos>.offset(direction, distance)`
- Description: This returns a new Pos with the current pos x, y, and z offset by a direction and a distance
- Parameters:
  - String (`direction`): the direction to offset by, must be one of: north, south, east, west, up, down
  - Number (`distance`): the distance to offset by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.offset('north', 2);
```

### `<Pos>.south()`
- Description: This returns a new Pos with the current pos z decremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.south();
```

### `<Pos>.south(number)`
- Description: This returns a new Pos with the current pos z decremented by the given number
- Parameter - Number (`number`): the number to decrement by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.south(2);
```

### `<Pos>.subtract(pos)`
- Description: This returns a new Pos with the current pos x, y, and z subtracted by the given pos x, y, and z
- Parameter - Pos (`pos`): the Pos to subtract by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.subtract(new Pos(2, 3, 5));
```

### `<Pos>.subtract(x, y, z)`
- Description: This returns a new Pos with the current pos x, y, and z subtracted by the given x, y, and z
- Parameters:
  - Number (`x`): the x subtractor
  - Number (`y`): the y subtractor
  - Number (`z`): the z subtractor
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.subtract(2, 3, 5);
```

### `<Pos>.toBlockPos()`
- Description: This floors all of the positions values to the nearest block
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.toBlockPos();
```

### `<Pos>.toList()`
- Description: This returns the Pos as a List containing the x, y, and z positions in order
- Returns - List: the Pos as a List
- Example:
```kotlin
x, y, z = pos.toList();
```

### `<Pos>.up()`
- Description: This returns a new Pos with the current pos y incremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.up();
```

### `<Pos>.up(number)`
- Description: This returns a new Pos with the current pos y incremented by the given number
- Parameter - Number (`number`): the number to increment by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.up(2);
```

### `<Pos>.west()`
- Description: This returns a new Pos with the current pos x decremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.west();
```

### `<Pos>.west(number)`
- Description: This returns a new Pos with the current pos x decremented by the given number
- Parameter - Number (`number`): the number to decrement by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.west(2);
```



# Recipe class
Recipe class for Arucas

This class represents recipes in Minecraft.
Import with `import Recipe from Minecraft;`

## Methods

### `<Recipe>.getCraftingType()`
- Description: This returns the crafting type of the recipe
- Returns - String: the crafting type of the recipe, for example: 'crafting', 'smelting', 'blasting'
- Example:
```kotlin
recipe.getCraftingType()
```

### `<Recipe>.getFullId()`
- Description: This returns the full id of the recipe
- Returns - String: the full id of the recipe
- Example:
```kotlin
recipe.getFullId()
```

### `<Recipe>.getId()`
- Description: This returns the id of the recipe
- Returns - String: the id of the recipe
- Example:
```kotlin
recipe.getId()
```

### `<Recipe>.getIngredients()`
- Description: This returns all the possible ingredients of the recipe
- Returns - List: list of lists, each inner lists contains possible recipe items
- Example:
```kotlin
recipe.getIngredients()
```

### `<Recipe>.getOutput()`
- Description: This returns the output of the recipe
- Returns - ItemStack: the output of the recipe
- Example:
```kotlin
recipe.getOutput()
```

## Static Methods

### `Recipe.of(recipeId)`
- Description: This converts a recipe id into a Recipe if it's valid,
otherwise an error will be thrown
- Parameter - String (`recipeId`): the id of the recipe to convert to a Recipe
- Returns - Recipe: the recipe instance from the id
- Example:
```kotlin
Recipe.of('redstone_block')
```



# Screen class
Screen class for Arucas

This allows you to get information about the player's current screen.
Import with `import Screen from Minecraft;`

## Methods

### `<Screen>.getName()`
- Description: Gets the name of the specific screen
- Returns - String: the screen name, if you are in the creative menu it will return the name of the tab you are on
- Example:
```kotlin
screen.getName()
```

### `<Screen>.getTitle()`
- Description: Gets the title of the specific screen
- Returns - String: the screen title as text, this may include formatting, and custom names for the screen if applicable
- Example:
```kotlin
screen.getTitle()
```



# Set class
Set class for Arucas

Sets are collections of unique values. Similar to maps, without the values.
An instance of the class can be created by using `Set.of(values...)`
Class does not need to be imported

## Constructors

### `new Set()`
- Description: This creates an empty set
- Example:
```kotlin
new Set();
```

## Methods

### `<Set>.add(value)`
- Description: This allows you to add a value to the set
- Parameter - Object (`value`): the value you want to add to the set
- Returns - Boolean: whether the value was successfully added to the set
- Example:
```kotlin
Set.of().add('object');
```

### `<Set>.addAll(collection)`
- Description: This allows you to add all the values in a collection into the set
- Parameter - Collection (`collection`): the collection of values you want to add
- Returns - Set: the modified set
- Example:
```kotlin
Set.of().addAll(Set.of('object', 81, 96, 'case'));
```

### `<Set>.clear()`
- Description: This removes all values from inside the set
- Example:
```kotlin
Set.of('object').clear();
```

### `<Set>.contains(value)`
- Description: This allows you to check whether a value is in the set
- Parameter - Object (`value`): the value that you want to check in the set
- Returns - Boolean: whether the value is in the set
- Example:
```kotlin
Set.of('object').contains('object');
```

### `<Set>.containsAll(collection)`
- Description: This allows you to check whether a collection of values are all in the set
- Parameter - Collection (`collection`): the collection of values you want to check in the set
- Returns - Boolean: whether all the values are in the set
- Example:
```kotlin
Set.of('object').containsAll(Set.of('object', 81, 96, 'case'));
```

### `<Set>.filter(function)`
- Description: This allows you to filter the set
- Parameter - Function (`function`): the function you want to filter the set by
- Returns - Set: the filtered set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).filter(fun(value) { return value > 80; });
```

### `<Set>.get(value)`
- Description: This allows you to get a value from in the set.
The reason this might be useful is if you want to retrieve something
from the set that will have the same hashcode but be in a different state
as the value you are passing in
- Parameter - Object (`value`): the value you want to get from the set
- Returns - Object: the value you wanted to get, null if it wasn't in the set
- Example:
```kotlin
Set.of('object').get('object');
```

### `<Set>.map(function)`
- Description: This allows you to map the set
- Parameter - Function (`function`): the function you want to map the set by
- Returns - Set: the mapped set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).map(fun(value) { return value * 2; });
```

### `<Set>.reduce(function)`
- Description: This allows you to reduce the set
- Parameter - Function (`function`): the function you want to reduce the set by
- Returns - Object: the reduced set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).reduce(fun(value, next) { return value + next; });
```

### `<Set>.remove(value)`
- Description: This allows you to remove a value from the set
- Parameter - Object (`value`): the value you want to remove from the set
- Returns - Boolean: whether the value was removed from the set
- Example:
```kotlin
Set.of('object').remove('object');
```

### `<Set>.removeAll(value)`
- Description: This allows you to remove all values in a collection from the set
- Parameter - Collection (`value`): the values you want to remove from the set
- Returns - Set: the set with the values removed
- Example:
```kotlin
Set.of('object', 'object').removeAll(Set.of('object'));
```

### `<Set>.toList()`
- Description: This returns a list of all the values in the set
- Returns - List: the list of values in the set
- Example:
```kotlin
Set.of('object', 81, 96, 'case').toList();
```

## Static Methods

### `Set.of(values...)`
- Description: This allows you to create a set with an arbitrary number of values
- Parameter - Object (`values...`): the values you want to add to the set
- Returns - Set: the set you created
- Example:
```kotlin
Set.of('object', 81, 96, 'case');
```

### `Set.unordered()`
- Description: This creates an unordered set
- Returns - Set: the unordered set
- Example:
```kotlin
Set.unordered();
```



# Shape class
Shape class for Arucas

This class is the base class for all shapes that can be rendered,
providing the base functionality for all shapes
Import with `import Shape from Minecraft;`

## Methods

### `<Shape>.getBlue()`
- Description: This returns the blue value of the shape
- Returns - Number: the blue value of the shape
- Example:
```kotlin
shape.getBlue();
```

### `<Shape>.getGreen()`
- Description: This returns the green value of the shape
- Returns - Number: the green value of the shape
- Example:
```kotlin
shape.getGreen();
```

### `<Shape>.getOpacity()`
- Description: This returns the opacity of the shape
- Returns - Number: the opacity of the shape
- Example:
```kotlin
shape.getOpacity();
```

### `<Shape>.getRGB()`
- Description: This returns the RGB value of the shape
- Returns - Number: the RGB value of the shape as a single number in the form 0xRRGGBB
- Example:
```kotlin
shape.getRGB();
```

### `<Shape>.getRGBAList()`
- Description: This returns the RGBA value of the shape as a list
- Returns - List: the RGBA value of the shape as a list in the form [red, green, blue, opacity]
- Example:
```kotlin
r, g, b, a = shape.getRGBAList();
```

### `<Shape>.getRGBList()`
- Description: This returns the RGB value of the shape as a list
- Returns - List: the RGB value of the shape as a list in the form [red, green, blue]
- Example:
```kotlin
r, g, b = shape.getRGBList();
```

### `<Shape>.getRed()`
- Description: This returns the red value of the shape
- Returns - Number: the red value of the shape
- Example:
```kotlin
shape.getRed();
```

### `<Shape>.getXScale()`
- Description: This gets the x scale of the shape
- Example:
```kotlin
shape.getXScale();
```

### `<Shape>.getXTilt()`
- Description: This gets the x tilt of the shape
- Example:
```kotlin
shape.getXTilt();
```

### `<Shape>.getYScale()`
- Description: This gets the y scale of the shape
- Example:
```kotlin
shape.getYScale();
```

### `<Shape>.getYTilt()`
- Description: This gets the y tilt of the shape
- Example:
```kotlin
shape.getYTilt();
```

### `<Shape>.getZScale()`
- Description: This gets the z scale of the shape
- Example:
```kotlin
shape.getZScale();
```

### `<Shape>.getZTilt()`
- Description: This gets the z tilt of the shape
- Example:
```kotlin
shape.getZTilt();
```

### `<Shape>.render()`
- Description: This sets the shape to be rendered indefinitely, the shape will only stop rendering when
the script ends or when you call the stopRendering() method
- Example:
```kotlin
shape.render();
```

### `<Shape>.setBlue(blue)`
- Description: This sets the blue value of the shape, using a single value.
If the colour is not between 0 and 255 an error will be thrown
- Parameter - Number (`blue`): the amount of blue between 0 - 255
- Example:
```kotlin
shape.setBlue(34);
```

### `<Shape>.setColour(colour)`
- Description: This sets the colour of the shape, using a single value, this
function also has a sibling named `setColor()` that has the same functionality.
The colour generally should be hexadecimal in the form 0xRRGGBB
- Parameter - Number (`colour`): the colour you want to set
- Example:
```kotlin
shape.setColour(0xFF0000);
```

### `<Shape>.setColour(red, green, blue)`
- Description: This sets the colour of the shape, using three values this function
also has a sibling named `setColor()` that has the same functionality.
If the colours are not between 0 and 255 an error will be thrown
- Parameters:
  - Number (`red`): the amount of red 0 - 255
  - Number (`green`): the amount of green 0 - 255
  - Number (`blue`): the amount of blue 0 - 255
- Example:
```kotlin
shape.setColour(34, 55, 0);
```

### `<Shape>.setGreen(green)`
- Description: This sets the green value of the shape, using a single value.
If the colour is not between 0 and 255 an error will be thrown
- Parameter - Number (`green`): the amount of green between 0 - 255
- Example:
```kotlin
shape.setGreen(34);
```

### `<Shape>.setOpacity(opacity)`
- Description: This sets the opacity of the shape, using a single value.
If the colour is not between 0 and 255 an error will be thrown
- Parameter - Number (`opacity`): the amount of opacity between 0 - 255
- Example:
```kotlin
shape.setOpacity(34);
```

### `<Shape>.setRed(red)`
- Description: This sets the red value of the shape, using a single value.
If the colour is not between 0 and 255 an error will be thrown
- Parameter - Number (`red`): the amount of red between 0 - 255
- Example:
```kotlin
shape.setRed(34);
```

### `<Shape>.setRenderThroughBlocks(boolean)`
- Description: This sets whether the shape should render through blocks
- Parameter - Boolean (`boolean`): whether the shape should render through blocks
- Example:
```kotlin
shape.setRenderThroughBlocks(true);
```

### `<Shape>.setScale(xScale, yScale, zScale)`
- Description: This sets the scale of the shape
- Parameters:
  - Number (`xScale`): the x scale of the shape
  - Number (`yScale`): the y scale of the shape
  - Number (`zScale`): the z scale of the shape
- Example:
```kotlin
shape.setScale(1.5, 2.5, 3.5);
```

### `<Shape>.setTilt(xTilt, yTilt, zTilt)`
- Description: This sets the tilt of the shape
- Parameters:
  - Number (`xTilt`): the x tilt
  - Number (`yTilt`): the y tilt
  - Number (`zTilt`): the z tilt
- Example:
```kotlin
shape.setTilt(100, 0, 80);
```

### `<Shape>.setXScale(xScale)`
- Description: This sets the x scale of the shape
- Parameter - Number (`xScale`): the x scale of the shape
- Example:
```kotlin
shape.setXScale(1.5);
```

### `<Shape>.setXTilt(xTilt)`
- Description: This sets the x tilt of the shape
- Parameter - Number (`xTilt`): the x tilt
- Example:
```kotlin
shape.setXTilt(100);
```

### `<Shape>.setYScale(yScale)`
- Description: This sets the y scale of the shape
- Parameter - Number (`yScale`): the y scale of the shape
- Example:
```kotlin
shape.setYScale(2.5);
```

### `<Shape>.setYTilt(yTilt)`
- Description: This sets the y tilt of the shape
- Parameter - Number (`yTilt`): the y tilt
- Example:
```kotlin
shape.setYTilt(0);
```

### `<Shape>.setZScale(zScale)`
- Description: This sets the z scale of the shape
- Parameter - Number (`zScale`): the z scale of the shape
- Example:
```kotlin
shape.setZScale(3.5);
```

### `<Shape>.setZTilt(zTilt)`
- Description: This sets the z tilt of the shape
- Parameter - Number (`zTilt`): the z tilt
- Example:
```kotlin
shape.setZTilt(80);
```

### `<Shape>.shouldRenderThroughBlocks()`
- Description: This returns whether the shape should render through blocks
- Returns - Boolean: whether the shape should render through blocks
- Example:
```kotlin
shape.shouldRenderThroughBlocks();
```

### `<Shape>.stopRendering()`
- Description: This stops the shape from rendering
- Example:
```kotlin
shape.stopRendering();
```



# SphereShape class
SphereShape class for Arucas

This class is used to create a sphere shape which can be rendered in the world.
Import with `import SphereShape from Minecraft;`

## Constructors

### `new SphereShape(pos)`
- Description: This creates a new sphere shape
- Parameter - Pos (`pos`): The position of the sphere
- Example:
```kotlin
new SphereShape(new Pos(0, 10, 0));
```

### `new SphereShape(x, y, z)`
- Description: This creates a new sphere shape
- Parameters:
  - Number (`x`): The x position of the sphere
  - Number (`y`): The y position of the sphere
  - Number (`z`): The z position of the sphere
- Example:
```kotlin
new SphereShape(0, 10, 0);
```

## Methods

### `<SphereShape>.getSteps()`
- Description: This gets the number of steps the sphere will take to render
- Returns - Number: The number of steps
- Example:
```kotlin
sphere.getSteps();
```

### `<SphereShape>.setSteps(steps)`
- Description: This sets the number of steps the sphere will take to render
- Parameter - Number (`steps`): The number of steps
- Example:
```kotlin
sphere.setSteps(30);
```



# String class
String class for Arucas

This class represents an array of characters to form a string.
This class cannot be instantiated directly, instead use the literal
by using quotes. Strings are immutable in Arucas.
Class does not need to be imported

## Constructors

### `new String()`
- Description: This creates a new string object, not from the string pool, with the given string.
This cannot be called directly, only from child classes
- Example:
```kotlin
class ChildString: String {
    ChildString(): super("example");
}
```

## Methods

### `<String>.capitalize()`
- Description: This returns the string in capitalized form
- Returns - String: the string in capitalized form
- Example:
```kotlin
'hello'.capitalize(); // 'Hello'
```

### `<String>.chars()`
- Description: This makes a list of all the characters in the string
- Returns - List: the list of characters
- Example:
```kotlin
'hello'.chars(); // ['h', 'e', 'l', 'l', 'o']
```

### `<String>.contains(string)`
- Description: This returns whether the string contains the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string contains the given string
- Example:
```kotlin
'hello'.contains('lo'); // true
```

### `<String>.endsWith(string)`
- Description: This returns whether the string ends with the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string ends with the given string
- Example:
```kotlin
'hello'.endsWith('lo'); // true
```

### `<String>.find(regex)`
- Description: This finds all matches of the regex in the string,
this does not find groups, for that use `<String>.findGroups(regex)`
- Parameter - String (`regex`): the regex to search the string with
- Returns - List: the list of all instances of the regex in the string
- Example:
```kotlin
'102i 1i'.find('([\\d+])i'); // ['2i', '1i']
```

### `<String>.findAll(regex)`
- Description: This finds all matches and groups of a regex in the matches in the string
the first group of each match will be the complete match and following
will be the groups of the regex, a group may be empty if it doesn't exist
- Parameter - String (`regex`): the regex to search the string with
- Returns - List: a list of match groups, which is a list containing matches
- Example:
```kotlin
'102i 1i'.findAll('([\\d+])i'); // [['2i', '2'], ['1i', '1']]
```

### `<String>.format(objects...)`
- Description: This formats the string using the given arguments.
This internally uses the Java String.format() method.
For how to use see here: https://www.javatpoint.com/java-string-format
- Parameter - Object (`objects...`): the objects to insert
- Returns - String: the formatted string
- Example:
```kotlin
'%s %s'.format('hello', 'world'); // 'hello world'
```

### `<String>.length()`
- Description: This returns the length of the string
- Returns - Number: the length of the string
- Example:
```kotlin
'hello'.length(); // 5
```

### `<String>.lowercase()`
- Description: This returns the string in lowercase
- Returns - String: the string in lowercase
- Example:
```kotlin
'HELLO'.lowercase(); // 'hello'
```

### `<String>.matches(regex)`
- Description: This returns whether the string matches the given regex
- Parameter - String (`regex`): the regex to match the string with
- Returns - Boolean: whether the string matches the given regex
- Example:
```kotlin
'foo'.matches('f.*'); // true
```

### `<String>.replaceAll(regex, replacement)`
- Description: This replaces all the instances of a regex with the replace string
- Parameters:
  - String (`regex`): the regex you want to replace
  - String (`replacement`): the string you want to replace it with
- Returns - String: the modified string
- Example:
```kotlin
'hello'.replaceAll('l', 'x'); // 'hexxo'
```

### `<String>.replaceFirst(regex, replacement)`
- Description: This replaces the first instance of a regex with the replace string
- Parameters:
  - String (`regex`): the regex you want to replace
  - String (`replacement`): the string you want to replace it with
- Returns - String: the modified string
- Example:
```kotlin
'hello'.replaceFirst('l', 'x'); // 'hexlo'
```

### `<String>.reverse()`
- Description: This returns the string in reverse
- Returns - String: the string in reverse
- Example:
```kotlin
'hello'.reverse(); // 'olleh'
```

### `<String>.split(regex)`
- Description: This splits the string into a list of strings based on a regex
- Parameter - String (`regex`): the regex to split the string with
- Returns - List: the list of strings
- Example:
```kotlin
'foo/bar/baz'.split('/');
```

### `<String>.startsWith(string)`
- Description: This returns whether the string starts with the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string starts with the given string
- Example:
```kotlin
'hello'.startsWith('he'); // true
```

### `<String>.strip()`
- Description: This strips the whitespace from the string
- Returns - String: the stripped string
- Example:
```kotlin
'  hello  '.strip(); // 'hello'
```

### `<String>.subString(from, to)`
- Description: This returns a substring of the string
- Parameters:
  - Number (`from`): the start index (inclusive)
  - Number (`to`): the end index (exclusive)
- Returns - String: the substring
- Example:
```kotlin
'hello'.subString(1, 3); // 'el'
```

### `<String>.toList()`
- Deprecated: Use '<String>.chars()' instead
- Description: This makes a list of all the characters in the string
- Returns - List: the list of characters
- Example:
```kotlin
'hello'.toList(); // ['h', 'e', 'l', 'l', 'o']
```

### `<String>.toNumber()`
- Description: This tries to convert the string to a number.
This method can convert hex or denary into numbers.
If the string is not a number, it will throw an error
- Returns - Number: the number
- Example:
```kotlin
'99'.toNumber(); // 99
```

### `<String>.uppercase()`
- Description: This returns the string in uppercase
- Returns - String: the string in uppercase
- Example:
```kotlin
'hello'.uppercase(); // 'HELLO'
```



# Task class
Task class for Arucas

This class is used to create tasks that can be chained and
run asynchronously. Tasks can be executed as many times as needed
and chained tasks will be executed in the order they are created.
Class does not need to be imported

## Constructors

### `new Task()`
- Description: This creates a new empty task
- Example:
```kotlin
task = new Task();
```

## Methods

### `<Task>.loopIf(boolSupplier)`
- Description: This loops the task, essentially just calling 'task.run', the
task will run async from the original task, the loop will continue
if the function provided returns true
- Parameter - Function (`boolSupplier`): the function to check if the loop should run
- Returns - Task: the task, this allows for chaining
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .loopIf(fun() true); // Always loop
```

### `<Task>.run()`
- Description: This runs the task asynchronously and returns a future which can be awaited.
The last function in the task will be used as the return value for the future
- Returns - Future: the future value that can be awaited
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .then(fun() 10);
f = task.run(); // prints 'hello world'
print(f.await()); // prints 10
```

### `<Task>.then(function)`
- Description: This adds a function to the end of the current task.
If this is the last function in the task then the return
value of the function will be the return value of the task.
- Parameter - Function (`function`): the function to run at the end of the task
- Returns - Task: the task, this allows for chaining
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .then(fun() 10);
f = task.run(); // prints 'hello world'
print(f.await()); // prints 10
```



# Text class
Text class for Arucas

This class is used to create formatted strings used inside Minecraft.
Import with `import Text from Minecraft;`

## Methods

### `<Text>.append(otherText)`
- Description: This allows you to append a text instance to another text instance
- Parameter - Text (`otherText`): the text instance to append to
- Returns - Text: the text instance with the appended text
- Example:
```kotlin
Text.of('Hello').append(Text.of(' world!'));
```

### `<Text>.format(formatting)`
- Description: This allows you to add a formatting to a text instance.
A list of formatting names can be found [here](https://minecraft.fandom.com/wiki/Formatting_codes).
This will throw an error if the formatting is invalid
- Parameter - String (`formatting`): the name of the formatting
- Returns - Text: the text instance with the formatting added
- Example:
```kotlin
text.format('DARK_RED').format('BOLD');
```

### `<Text>.withClickEvent(event, value)`
- Description: This allows you to add a click event to a text instance.
The possible events are: 'open_url', 'open_file', 'run_command', 'suggest_command', 'copy_to_clipboard', 'run_function'.
This will throw an error if the action is invalid
- Parameters:
  - String (`event`): the name of the event
  - String (`value`): the value associated with the event
- Returns - Text: the text instance with the click event
- Example:
```kotlin
text = Text.of("Hello World!");

// Examples of click events
text.withClickEvent("open_url", "https://youtu.be/dQw4w9WgXcQ");
text.withClickEvent("open_file", "C:/Users/user/Desktop/thing.txt");
text.withClickEvent("run_command", "/gamemode creative");
text.withClickEvent("suggest_command", "/gamemode survival");
text.withClickEvent("copy_to_clipboard", "Ooops!");
text.withClickEvent("run_function", fun() {
    print("Text was clicked!");
});
```

### `<Text>.withHoverEvent(event, value)`
- Description: This allows you to add a hover event to a text instance.
The possible events are: 'show_text', 'show_item', 'show_entity'.
This will throw an error if the event is invalid
- Parameters:
  - String (`event`): the name of the event
  - Object (`value`): the value associated with the event
- Returns - Text: the text instance with the hover event
- Example:
```kotlin
text = Text.of("Hello World!");

// Examples of hover events
text.withHoverEvent("show_text", Text.of("Hello world!"));
text.withHoverEvent("show_item", Material.DIAMOND_SWORD.asItemStack());
text.withHoverEvent("show_entity", Player.get());
```

## Static Methods

### `Text.of(string)`
- Description: This converts a string into a text instance
- Parameter - String (`string`): The string to convert into a text instance
- Returns - Text: the text instance from the string
- Example:
```kotlin
Text.of('Hello World!');
```

### `Text.parse(textJson)`
- Description: This converts a text json into a text instance
- Parameter - String (`textJson`): The string in json format, or a Json value itself
- Returns - Text: the text instance from the json
- Example:
```kotlin
Text.parse('{"text":"Hello World!","color":"white","italic":"true"}');
```



# Thread class
Thread class for Arucas

This class allows to to create threads for async executions.
This class cannot be instantiated or extended. To create a new
thread use the static method 'Thread.runThreaded()'
Class does not need to be imported

## Methods

### `<Thread>.freeze()`
- Description: This serves the same purpose as 'Thread.freeze()' however this works on the current
thread instance, unlike 'Thread.freeze()' this cannot throw an error.
- Example:
```kotlin
Thread.getCurrentThread().freeze()
```

### `<Thread>.getAge()`
- Description: This gets the age of the thread in milliseconds
- Returns - Number: the age of the thread
- Example:
```kotlin
Thread.getCurrentThread().getAge();
```

### `<Thread>.getName()`
- Description: This gets the name of the thread
- Returns - String: the name of the thread
- Example:
```kotlin
Thread.getCurrentThread().getName();
```

### `<Thread>.isAlive()`
- Description: This checks if the thread is alive (still running)
- Returns - Boolean: true if the thread is alive, false if not
- Example:
```kotlin
Thread.getCurrentThread().isAlive();
```

### `<Thread>.stop()`
- Description: This stops the thread from executing, anything that was running will be instantly stopped.
This method will fail if the thread is not alive
- Example:
```kotlin
Thread.getCurrentThread().stop();
```

### `<Thread>.thaw()`
- Description: This will thaw the thread from its frozen state, if the thread is not frozen then an
error will be thrown
- Example:
```kotlin
Thread.getCurrentThread().thaw();
```

## Static Methods

### `Thread.freeze()`
- Description: This freezes the current thread, stops anything else from executing on the thread.
This may fail if you try to freeze a non Arucas Thread in which case an error will be thrown
- Example:
```kotlin
Thread.freeze();
```

### `Thread.getCurrentThread()`
- Description: This gets the current thread that the code is running on,
this may throw an error if the thread is not safe to get,
which happens when running outside of Arucas Threads
- Returns - Thread: the current thread
- Example:
```kotlin
Thread.getCurrentThread();
```

### `Thread.runThreaded(function)`
- Description: This starts a new thread and runs a function on it, the thread will
terminate when it finishes executing the function, threads will stop automatically
when the program stops, you are also able to stop threads by using the Thread object
- Parameter - Function (`function`): the function you want to run on a new thread
- Returns - Thread: the new thread
- Example:
```kotlin
Thread.runThreaded(fun() {
    print("Running asynchronously!");
});
```

### `Thread.runThreaded(name, function)`
- Description: This starts a new thread with a specific name and runs a function on it
- Parameters:
  - String (`name`): the name of the thread
  - Function (`function`): the function you want to run on a new thread
- Returns - Thread: the new thread
- Example:
```kotlin
Thread.runThreaded("MyThread", fun() {
    print("Running asynchronously on MyThread");
});
```



# Trade class
Trade class for Arucas

This class represents a trade offer, and allows you to get information about it.
Import with `import Trade from Minecraft;`

## Methods

### `<Trade>.getAdjustedFirstBuyItem()`
- Description: Gets the first item that the merchant will buy, adjusted by the price multiplier
- Returns - ItemStack: the first item to buy
- Example:
```kotlin
trade.getAdjustedFirstBuyItem();
```

### `<Trade>.getFirstBuyItem()`
- Description: Gets the first item that the merchant will buy
- Returns - ItemStack: the first item to buy
- Example:
```kotlin
trade.getFirstBuyItem();
```

### `<Trade>.getMaxUses()`
- Description: Gets the maximum number of times the trade can be used
- Returns - Number: the maximum number of uses
- Example:
```kotlin
trade.getMaxUses();
```

### `<Trade>.getPriceMultiplier()`
- Description: Gets the price multiplier which is used to adjust the price of the first buy item
- Returns - Number: the price multiplier
- Example:
```kotlin
trade.getPriceMultiplier();
```

### `<Trade>.getSecondBuyItem()`
- Description: Gets the second item that the merchant will buy
- Returns - ItemStack: the second item to buy
- Example:
```kotlin
trade.getSecondBuyItem();
```

### `<Trade>.getSellItem()`
- Description: Gets the item that is being sold by the merchant
- Returns - ItemStack: the item for sale
- Example:
```kotlin
trade.getSellItem();
```

### `<Trade>.getSpecialPrice()`
- Description: This gets the special price which is used to adjust the price of the first buy item
- Returns - Number: the special price
- Example:
```kotlin
trade.getSpecialPrice();
```

### `<Trade>.getUses()`
- Description: Gets the number of times the trade has been used
- Returns - Number: the number of uses
- Example:
```kotlin
trade.getUses();
```

### `<Trade>.getXpReward()`
- Description: Returns the amount of xp the villager will get, which
goes towards them levelling up, from trading this offer
- Returns - Number: the amount of xp
- Example:
```kotlin
trade.getXpReward
```



# Type class
Type class for Arucas

This class lets you get the type of another class
Class does not need to be imported

## Methods

### `<Type>.getName()`
- Description: This gets the name of the type
- Returns - String: the name of the type
- Example:
```kotlin
String.type.getName();
```

### `<Type>.inheritsFrom(type)`
- Description: This checks whether a type is a subtype of another type
- Parameter - Type (`type`): the other type you want to check against
- Returns - Boolean: whether the type is of that type
- Example:
```kotlin
String.type.inheritsFrom(Number.type);
```

## Static Methods

### `Type.of(value)`
- Description: This gets the specific type of a value
- Parameter - Object (`value`): the value you want to get the type of
- Returns - Type: the type of the value
- Example:
```kotlin
Type.of(0);
```



# World class
World class for Arucas

This class represents worlds, and allows you to interact with things inside of them.
Import with `import World from Minecraft;`

## Methods

### `<World>.getAllEntities()`
- Description: This will get all entities in the world
- Returns - List: a list of all entities
- Example:
```kotlin
world.getAllEntities();
```

### `<World>.getAllOtherPlayers()`
- Deprecated: Use '<World>.getAllPlayers()' instead
- Description: This will get all other players in the world
- Returns - List: a list of all other players
- Example:
```kotlin
world.getAllOtherPlayers();
```

### `<World>.getAllPlayers()`
- Description: This function gets all players in the world that are loaded
- Returns - List: all players in the world
- Example:
```kotlin
world.getAllPlayers();
```

### `<World>.getArea(pos1, pos2)`
- Deprecated: This function is memory intensive, you should use `<World>.getPositions(pos1, pos2)`
- Description: This gets a list of all block positions between the two positions
- Parameters:
  - Pos (`pos1`): the first position
  - Pos (`pos2`): the second position
- Returns - List: the list of positions
- Example:
```kotlin
world.getArea(new Pos(0, 100, 0), new Pos(0, 100, 0));
```

### `<World>.getAreaOfBlocks(pos1, pos2)`
- Deprecated: This function is memory intensive, you should use `<World>.getBlocks(pos1, pos2)`
- Description: This gets a list of all blocks (with positions) between the two positions
- Parameters:
  - Pos (`pos1`): the first position
  - Pos (`pos2`): the second position
- Returns - List: the list of blocks
- Example:
```kotlin
world.getAreaOfBlocks(new Pos(0, 100, 0), new Pos(0, 100, 0));
```

### `<World>.getBiomeAt(pos)`
- Description: This function gets the biome at the given coordinates
- Parameter - Pos (`pos`): the position
- Returns - Biome: the biome at the given coordinates
- Example:
```kotlin
world.getBiomeAt(new Pos(0, 100, 0));
```

### `<World>.getBiomeAt(x, y, z)`
- Description: This function gets the biome at the given coordinates
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Biome: the biome at the given coordinates
- Example:
```kotlin
world.getBiomeAt(0, 100, 0);
```

### `<World>.getBlockAt(pos)`
- Description: This function gets the block at the given coordinates
- Parameter - Pos (`pos`): the position
- Returns - Block: the block at the given coordinates
- Example:
```kotlin
world.getBlockAt(new Pos(0, 100, 0));
```

### `<World>.getBlockAt(x, y, z)`
- Description: This function gets the block at the given coordinates
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Block: the block at the given coordinates
- Example:
```kotlin
world.getBlockAt(0, 100, 0);
```

### `<World>.getBlockLight(pos)`
- Description: Gets the block light at the given position ignoring sky light
- Parameter - Pos (`pos`): the position of the block
- Returns - Number: the light level between 0 - 15
- Example:
```kotlin
world.getBlockLight(new Pos(0, 0, 0));
```

### `<World>.getBlocks(pos1, pos2)`
- Description: This gets an iterator for all blocks (and positions) between two positions
- Parameters:
  - Pos (`pos1`): the first position
  - Pos (`pos2`): the second position
- Returns - Iterable: the iterator for the blocks
- Example:
```kotlin
foreach (block : world.getBlocks(new Pos(0, 100, 100), new Pos(0, 100, 0)));
```

### `<World>.getBlocksFromCentre(centre, xRange, yRange, zRange)`
- Description: This gets an iterator for all blocks (and positions) between two positions.
The iterator iterates from the centre outwards
- Parameters:
  - Pos (`centre`): the central position
  - Number (`xRange`): how far to iterate on the x axis
  - Number (`yRange`): how far to iterate on the y axis
  - Number (`zRange`): how far to iterate on the z axis
- Returns - Iterable: the iterator for the blocks
- Example:
```kotlin
foreach (block : world.getBlocksFromCentre(new Pos(0, 100, 100), 10, 5, 60));
```

### `<World>.getClosestPlayer(entity, maxDistance)`
- Description: This will get the closest player to another entity in the world
- Parameters:
  - Entity (`entity`): the entity to get the closest player to
  - Number (`maxDistance`): the maximum distance to search for a player in blocks
- Returns - Player: the closest player, null if not found
- Example:
```kotlin
world.getClosestPlayer(Player.get(), 100);
```

### `<World>.getDimensionName()`
- Deprecated: You should use 'world.getId()' instead
- Description: This will get the id of the world
- Returns - String: the id of the world, for example: 'overworld'
- Example:
```kotlin
world.getDimensionName();
```

### `<World>.getEmittedRedstonePower(pos, direction)`
- Description: Gets the emitted restone power at the given position and direction
- Parameters:
  - Pos (`pos`): the position of the block
  - String (`direction`): the direction to check, for example 'north', 'east', 'up', etc.
- Returns - Number: the emitted redstone power
- Example:
```kotlin
world.getEmittedRedstonePower(new Pos(0, 100, 0), 'north');
```

### `<World>.getEmittedRedstonePower(x, y, z, direction)`
- Description: Gets the emitted restone power at the given position and direction
- Parameters:
  - Number (`x`): the x position of the block
  - Number (`y`): the y position of the block
  - Number (`z`): the z position of the block
  - String (`direction`): the direction to check, for example 'north', 'east', 'up', etc.
- Returns - Number: the emitted redstone power
- Example:
```kotlin
world.getEmittedRedstonePower(0, 100, 0, 'north');
```

### `<World>.getEntityFromId(entityId)`
- Description: This will get an entity from the given entity id
- Parameter - Number (`entityId`): the entity id
- Returns - Entity: the entity, null if not found
- Example:
```kotlin
world.getEntityFromId(1);
```

### `<World>.getFullId()`
- Description: This will get the full id of the world
- Returns - String: the full id of the world, for example: 'minecraft:overworld'
- Example:
```kotlin
world.getFullId();
```

### `<World>.getId()`
- Description: This will get the id of the world
- Returns - String: the id of the world, for example: 'overworld'
- Example:
```kotlin
world.getId();
```

### `<World>.getLight(pos)`
- Description: Gets the light level at the given position, takes the max of either sky light of block light
- Parameter - Pos (`pos`): the position of the block
- Returns - Number: the light level between 0 - 15
- Example:
```kotlin
world.getLight(new Pos(0, 100, 0));
```

### `<World>.getLight(x, y, z)`
- Description: Gets the light level at the given position, takes the max of either sky light of block light
- Parameters:
  - Number (`x`): the x position of the block
  - Number (`y`): the y position of the block
  - Number (`z`): the z position of the block
- Returns - Number: the light level between 0 - 15
- Example:
```kotlin
world.getLight(0, 100, 0);
```

### `<World>.getOtherPlayer(username)`
- Deprecated: Use '<World>.getPlayer(name)' instead
- Description: This gets another player from the given username
- Parameter - String (`username`): the username of the other player
- Returns - Player: the other player, null if not found
- Example:
```kotlin
world.getOtherPlayer('senseiwells');
```

### `<World>.getPlayer(name)`
- Description: This function gets the player with the given name
- Parameter - String (`name`): the name of the player
- Returns - Player: the player with the given name
- Example:
```kotlin
world.getPlayer('player');
```

### `<World>.getPositions(pos1, pos2)`
- Description: This gets an iterator for all positions between two positions
- Parameters:
  - Pos (`pos1`): the first position
  - Pos (`pos2`): the second position
- Returns - Iterable: the iterator for the positions
- Example:
```kotlin
foreach (pos : world.getPositions(new Pos(0, 100, 100), new Pos(0, 100, 0)));
```

### `<World>.getPositionsFromCentre(centre, xRange, yRange, zRange)`
- Description: This gets an iterator for all positions between two positions.
The iterator iterates from the centre outwards
- Parameters:
  - Pos (`centre`): the central position
  - Number (`xRange`): how far to iterate on the x axis
  - Number (`yRange`): how far to iterate on the y axis
  - Number (`zRange`): how far to iterate on the z axis
- Returns - Iterable: the iterator for the positions
- Example:
```kotlin
foreach (pos : world.getPositionsFromCentre(new Pos(0, 100, 100), 10, 10, 10));
```

### `<World>.getSkyLight(pos)`
- Description: Gets the sky light at the given position ignoring block light
- Parameter - Pos (`pos`): the position of the block
- Returns - Number: the light level between 0 - 15
- Example:
```kotlin
world.getSkyLight(new Pos(0, 0, 0));
```

### `<World>.getTimeOfDay()`
- Description: This will get the time of day of the world
info on the time of day [here](https://minecraft.fandom.com/wiki/Daylight_cycle)
- Returns - Number: the time of day of the world, between 0 and 24000
- Example:
```kotlin
world.getTimeOfDay();
```

### `<World>.isAir(pos)`
- Description: Returns true if the block at the given position is air
- Parameter - Pos (`pos`): the position of the block
- Returns - Boolean: true if the block is air
- Example:
```kotlin
world.isAir(new Pos(0, 100, 0));
```

### `<World>.isAir(x, y, z)`
- Description: Returns true if the block at the given position is air
- Parameters:
  - Number (`x`): the x position of the block
  - Number (`y`): the y position of the block
  - Number (`z`): the z position of the block
- Returns - Boolean: true if the block is air
- Example:
```kotlin
world.isAir(0, 100, 0);
```

### `<World>.isLoaded(pos)`
- Description: This function returns loaded state of given coordinates(client side)
- Parameter - Pos (`pos`): the position
- Returns - Boolean: whether the block is loaded at the given coordinates
- Example:
```kotlin
world.isLoaded(new Pos(0, 100, 0));
```

### `<World>.isRaining()`
- Description: This will check if the world is currently raining
- Returns - Boolean: true if the world is currently raining
- Example:
```kotlin
world.isRaining();
```

### `<World>.isThundering()`
- Description: This will check if the world is currently thundering
- Returns - Boolean: true if the world is currently thundering
- Example:
```kotlin
world.isThundering();
```

### `<World>.renderParticle(particleId, pos)`
- Description: This will render a particle in the world, you can find a list of all
the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),
this will throw an error if the id is invalid
- Parameters:
  - String (`particleId`): the id of the particle
  - Pos (`pos`): the position of the particle
- Example:
```kotlin
world.renderParticle('end_rod', pos);
```

### `<World>.renderParticle(particleId, x, y, z)`
- Description: This will render a particle in the world, you can find a list of all
the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),
if the id is invalid it will throw an error
- Parameters:
  - String (`particleId`): the id of the particle
  - Number (`x`): the x position of the particle
  - Number (`y`): the y position of the particle
  - Number (`z`): the z position of the particle
- Example:
```kotlin
world.renderParticle('end_rod', 10, 10, 10);
```

### `<World>.renderParticle(particleId, pos, velX, velY, velZ)`
- Description: This will render a particle in the world with a velocity, you can find a list of all
the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),
this will throw an error if the id is invalid
- Parameters:
  - String (`particleId`): the id of the particle
  - Pos (`pos`): the position of the particle
  - Number (`velX`): the velocity of the particle on the x axis
  - Number (`velY`): the velocity of the particle on the y axis
  - Number (`velZ`): the velocity of the particle on the z axis
- Example:
```kotlin
world.renderParticle('end_rod', pos, 0.5, 0.5, 0.5);
```

### `<World>.setGhostBlock(block, pos)`
- Deprecated: This function is dangerous, use at your own risk
- Description: This sets a ghost block in the world as if it were a real block, may cause issues
- Parameters:
  - Block (`block`): the block to set
  - Pos (`pos`): the position of the block
- Example:
```kotlin
world.setGhostBlock(Material.BEDROCK.asBlock(), new Pos(0, 100, 0));
```

### `<World>.setGhostBlock(block, x, y, z)`
- Deprecated: This function is dangerous, use at your own risk
- Description: This sets a ghost block in the world as if it were a real block, may cause issues
- Parameters:
  - Block (`block`): the block to set
  - Number (`x`): the x position of the block
  - Number (`y`): the y position of the block
  - Number (`z`): the z position of the block
- Example:
```kotlin
world.setGhostBlock(Material.BEDROCK.asBlock(), 0, 100, 0);
```



# Events

Events are triggers for certain events that happen in the game.
ClientScript provides a way to hook into these triggers to be able to run your code.
You can register multiple functions to an event and they will all get called.
See [here]() on how to register and unregister events.
Each event will run async by default but you are able to run it on the main game thread.


## `"onAttackEntity"`
- This event is fired when the player attacks an entity
- Parameter - Entity (`entity`): the entity that was attacked
- Cancellable: true
```kotlin
new GameEvent("onAttackEntity", fun(entity) {
    // Code
});
```


## `"onScriptPacket"`
- This event is fired when a script packet is received from scarpet on the server
- Parameter - List (`packet`): a list of data that was received
- Cancellable: false
```kotlin
new GameEvent("onScriptPacket", fun(packet) {
    // Code
});
```


## `"onCloseScreen"`
- This event is fired when the player closes a screen
- Parameter - Screen (`screen`): the screen that was just closed
- Cancellable: false
```kotlin
new GameEvent("onCloseScreen", fun(screen) {
    // Code
});
```


## `"onUse"`
- This event is fired when the player uses
- Cancellable: true
```kotlin
new GameEvent("onUse", fun() {
    // Code
});
```


## `"onClientTick"`
- The event is fired on every client tick
- Cancellable: false
```kotlin
new GameEvent("onClientTick", fun() {
    // Code
});
```


## `"onAttack"`
- This event is fired when the player attacks
- Cancellable: true
```kotlin
new GameEvent("onAttack", fun() {
    // Code
});
```


## `"onConnect"`
- The event is fired when the player connects to a server
- Parameter - Player (`player`): the player entity
- Parameter - World (`world`): the world the player joined
- Cancellable: false
```kotlin
new GameEvent("onConnect", fun(player, world) {
    // Code
});
```


## `"onDimensionChange"`
- This event is fired when the player changes their dimension
- Parameter - World (`world`): the new world
- Cancellable: false
```kotlin
new GameEvent("onDimensionChange", fun(world) {
    // Code
});
```


## `"onKeyRelease"`
- This event is fired when a key is released
- Parameter - String (`key`): the key that was released
- Cancellable: true
```kotlin
new GameEvent("onKeyRelease", fun(key) {
    // Code
});
```


## `"onDeath"`
- This event is fired when the player dies
- Parameter - Entity (`entity`): the entity that killed the player, may be null
- Parameter - Text (`message`): the death message
- Cancellable: false
```kotlin
new GameEvent("onDeath", fun(entity, message) {
    // Code
});
```


## `"onInteractItem"`
- This event is fired when the player interacts with an item
- Parameter - ItemStack (`itemStack`): the item stack that was interacted with
- Cancellable: true
```kotlin
new GameEvent("onInteractItem", fun(itemStack) {
    // Code
});
```


## `"onDisconnect"`
- This event is fired when the player disconnects from a server
- Cancellable: false
```kotlin
new GameEvent("onDisconnect", fun() {
    // Code
});
```


## `"onInteractEntity"`
- This event is fired when the player interacts with an entity
- Parameter - Entity (`entity`): the entity that was interacted with
- Parameter - ItemStack (`itemStack`): the item stack that was interacted with
- Cancellable: true
```kotlin
new GameEvent("onInteractEntity", fun(entity, itemStack) {
    // Code
});
```


## `"onEat"`
- This event is fired when the player eats something
- Parameter - ItemStack (`food`): the item stack that was eaten
- Cancellable: false
```kotlin
new GameEvent("onEat", fun(food) {
    // Code
});
```


## `"onEntitySpawn"`
- This event is fired when an entity spawns, this doesn't include mobs
- Parameter - Entity (`entity`): the entity that was spawned
- Cancellable: false
```kotlin
new GameEvent("onEntitySpawn", fun(entity) {
    // Code
});
```


## `"onAttackBlock"`
- This event is fired when the player attacks a block
- Parameter - Block (`block`): the block that was attacked
- Cancellable: true
```kotlin
new GameEvent("onAttackBlock", fun(block) {
    // Code
});
```


## `"onTotem"`
- This event is fired when the player uses a totem
- Cancellable: false
```kotlin
new GameEvent("onTotem", fun() {
    // Code
});
```


## `"onPlayerLook"`
- This event is fired when the player changes their yaw and/or pitch
- Parameter - Number (`yaw`): the player's yaw
- Parameter - Number (`pitch`): the player's pitch
- Cancellable: true
```kotlin
new GameEvent("onPlayerLook", fun(yaw, pitch) {
    // Code
});
```


## `"onAnvil"`
- This event is fired when the player anvils an item
- Parameter - ItemStack (`first`): the first input
- Parameter - ItemStack (`second`): the second input
- Parameter - ItemStack (`result`): the result of the first and second input
- Parameter - String (`newName`): the new name of the item stack
- Parameter - Number (`levelCost`): the amount of xp required
- Cancellable: false
```kotlin
new GameEvent("onAnvil", fun(first, second, result, newName, levelCost) {
    // Code
});
```


## `"onDropItem"`
- This event is fired when the player tries to drop an item
- Parameter - ItemStack (`itemStack`): the item that is trying to be dropped
- Cancellable: true
```kotlin
new GameEvent("onDropItem", fun(itemStack) {
    // Code
});
```


## `"onClickSlot"`
- This event is fired when the player clicks on a slot in their inventory
- Parameter - Number (`slot`): the slot number that was clicked
- Parameter - String (`action`): ths action that was used
- Cancellable: true
```kotlin
new GameEvent("onClickSlot", fun(slot, action) {
    // Code
});
```


## `"onInteractBlock"`
- This event is fired when the player interacts with a block
- Parameter - Block (`block`): the block the player is interacting with
- Parameter - ItemStack (`itemStack`): the item stack that was interacted with
- Cancellable: true
```kotlin
new GameEvent("onInteractBlock", fun(block, itemStack) {
    // Code
});
```


## `"onSendMessage"`
- This event is fired when the player sends a message in chat
- Parameter - String (`message`): the message that was sent
- Cancellable: true
```kotlin
new GameEvent("onSendMessage", fun(message) {
    // Code
});
```


## `"onHealthUpdate"`
- This event is fired when the player's health changes
- Parameter - Number (`health`): the new health
- Cancellable: false
```kotlin
new GameEvent("onHealthUpdate", fun(health) {
    // Code
});
```


## `"onFishBite"`
- This event is fired when a fish bites the player's fishing rod
- Parameter - Entity (`entity`): the fishing bobber entity
- Cancellable: false
```kotlin
new GameEvent("onFishBite", fun(entity) {
    // Code
});
```


## `"onEntityRemoved"`
- This event is fired when an entity is removed
- Parameter - Entity (`entity`): the entity that was removed
- Cancellable: false
```kotlin
new GameEvent("onEntityRemoved", fun(entity) {
    // Code
});
```


## `"onOpenScreen"`
- This event is fired when the player opens a screen
- Parameter - Screen (`screen`): the screen that was just opened
- Cancellable: false
```kotlin
new GameEvent("onOpenScreen", fun(screen) {
    // Code
});
```


## `"onPlayerJoin"`
- This event is fired when a player joins the server
- Parameter - String (`name`): the player's name
- Cancellable: false
```kotlin
new GameEvent("onPlayerJoin", fun(name) {
    // Code
});
```


## `"onBlockPlaced"`
- This event is fired when the player places a block
- Parameter - Block (`block`): the block that was placed
- Cancellable: false
```kotlin
new GameEvent("onBlockPlaced", fun(block) {
    // Code
});
```


## `"onMobSpawn"`
- This event is fired when a mob spawns
- Parameter - LivingEntity (`mob`): the mob that was spawned
- Cancellable: false
```kotlin
new GameEvent("onMobSpawn", fun(mob) {
    // Code
});
```


## `"onKeyPress"`
- This event is fired when a key is pressed
- Parameter - String (`key`): the key that was pressed
- Cancellable: true
```kotlin
new GameEvent("onKeyPress", fun(key) {
    // Code
});
```


## `"onClickRecipe"`
- This event is fired when the player clicks on a recipe in the recipe book
- Parameter - Recipe (`recipe`): the recipe that was clicked
- Cancellable: true
```kotlin
new GameEvent("onClickRecipe", fun(recipe) {
    // Code
});
```


## `"onScriptEnd"`
- This event is fired when the script is ends
- Cancellable: false
```kotlin
new GameEvent("onScriptEnd", fun() {
    // Code
});
```


## `"onMouseScroll"`
- This event is fired when the player scrolls
- Parameter - Number (`direction`): either -1 or 1 depending on the scroll direction
- Cancellable: true
```kotlin
new GameEvent("onMouseScroll", fun(direction) {
    // Code
});
```


## `"onPickBlock"`
- This event is fired when the player picks a block with middle mouse
- Parameter - ItemStack (`itemStack`): the item stack that was picked
- Cancellable: true
```kotlin
new GameEvent("onPickBlock", fun(itemStack) {
    // Code
});
```


## `"onReceiveMessage"`
- This event is fired when the player receives a message in chat
- Parameter - String (`uuid`): the sender's UUID
- Parameter - String (`message`): the message that was received
- Cancellable: true
```kotlin
new GameEvent("onReceiveMessage", fun(uuid, message) {
    // Code
});
```


## `"onGamemodeChange"`
- This event is fired when the player changes their gamemode
- Parameter - String (`gamemode`): the new gamemode
- Cancellable: false
```kotlin
new GameEvent("onGamemodeChange", fun(gamemode) {
    // Code
});
```


## `"onPlayerLeave"`
- This event is fired when a player leaves the server
- Parameter - String (`name`): the player's name
- Cancellable: false
```kotlin
new GameEvent("onPlayerLeave", fun(name) {
    // Code
});
```


## `"onPickUpItem"`
- This event is fired when the player picks up an item
- Parameter - ItemStack (`itemStack`): the item
- Cancellable: false
```kotlin
new GameEvent("onPickUpItem", fun(itemStack) {
    // Code
});
```


## `"onBlockBroken"`
- This event is fired when the player breaks a new block
- Parameter - Block (`block`): the block that was broken
- Cancellable: false
```kotlin
new GameEvent("onBlockBroken", fun(block) {
    // Code
});
```


## `"onBlockUpdate"`
- This event is fired when a block update is recieved on the client
- Parameter - Block (`block`): the block that was updated
- Cancellable: false
```kotlin
new GameEvent("onBlockUpdate", fun(block) {
    // Code
});
```


## `"onRespawn"`
- This event is fired when the player respawns
- Parameter - Player (`player`): the respawned player entity
- Cancellable: false
```kotlin
new GameEvent("onRespawn", fun(player) {
    // Code
});
```
