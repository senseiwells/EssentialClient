## BuiltInExtension

### `debug(bool)`
- Description: This is used to enable or disable debug mode.
- Parameter - Boolean (`bool`): True to enable debug mode, false to disable debug mode.
- Example:
```kotlin
debug(true);
```

### `eval(code)`
- Description: This is used to evaluate a string as code.
This will not inherit imports that are in the parent script.
- Parameter - String (`code`): The code to evaluate.
- Returns - Object: The result of the evaluation.
- Example:
```kotlin
eval('1 + 1');
```

### `experimental(bool)`
- Description: This is used to enable or disable experimental mode.
- Parameter - Boolean (`bool`): True to enable experimental mode, false to disable experimental mode.
- Example:
```kotlin
experimental(true);
```

### `getArucasVersion()`
- Description: This is used to get the version of Arucas that is currently running.
- Returns - String: The version of Arucas that is currently running.
- Example:
```kotlin
getArucasVersion();
```

### `getDate()`
- Description: This is used to get the current date formatted with dd/MM/yyyy in your local time.
- Returns - String: The current date formatted with dd/MM/yyyy.
- Example:
```kotlin
getDate();
```

### `getMilliTime()`
- Description: This is used to get the current time in milliseconds.
- Returns - Number: The current time in milliseconds.
- Example:
```kotlin
getMilliTime();
```

### `getNanoTime()`
- Description: This is used to get the current time in nanoseconds.
- Returns - Number: The current time in nanoseconds.
- Example:
```kotlin
getNanoTime();
```

### `getTime()`
- Description: This is used to get the current time formatted with HH:mm:ss in your local time.
- Returns - String: The current time formatted with HH:mm:ss.
- Example:
```kotlin
getTime();
```

### `getUnixTime()`
- Description: This is used to get the current time in seconds since the Unix epoch.
- Returns - Number: The current time in seconds since the Unix epoch.
- Example:
```kotlin
getUnixTime();
```

### `input(prompt)`
- Description: This is used to take an input from the user. The execution of
the script is paused until the user has inputted a value.
- Parameter - String (`prompt`): The prompt to show the user.
- Returns - String: The input from the user.
- Example:
```kotlin
name = input('What is your name?');
```

### `isDebug()`
- Description: This is used to determine whether the interpreter is in debug mode.
- Example:
```kotlin
isDebug();
```

### `isExperimental()`
- Description: This is used to determine whether the interpreter is in experimental mode.
- Example:
```kotlin
isExperimental();
```

### `isMain()`
- Description: This is used to check whether the script is the main script.
- Returns - Boolean: True if the script is the main script, false if it is not.
- Example:
```kotlin
isMain();
```

### `len(sizable)`
- Description: This is used to get the length of a collection or string.
- Parameter - String (`sizable`): The collection or string.
- Returns - Number: The size of the given object.
- Example:
```kotlin
len("Hello World");
```

### `print(value)`
- Description: This prints a value to the output handler.
- Parameter - Object (`value`): The value to print.
- Example:
```kotlin
print('Hello World');
```

### `print(value...)`
- Description: This prints a number of values to the console.
If there are no arguments then this will print a new line,
other wise it will print the contents without a new line.
- Parameter - Object (`value`): The value to print.
- Example:
```kotlin
print('Hello World', 'This is a test', 123); // prints 'Hello WorldThis is a test123'
```

### `printDebug(value)`
- Description: This logs something to the debug output.
It only prints if debug mode is enabled: `debug(true)`.
- Parameter - Object (`value`): The value to print.
- Example:
```kotlin
debug(true); // Enable debug for testing
if (true) {
    printDebug("Inside if statement");
}
```

### `random(bound)`
- Description: This is used to generate a random integer between 0 and the bound.
- Parameter - Number (`bound`): The maximum bound (exclusive).
- Returns - Number: The random integer.
- Example:
```kotlin
random(10);
```

### `range(bound)`
- Description: This is used to generate a range of integers starting from 0, incrementing by 1.
- Parameter - Number (`bound`): The maximum bound (exclusive).
- Returns - Iterable: An iterable object that returns the range of integers.
- Example:
```kotlin
range(10);
```

### `range(start, bound)`
- Description: This is used to generate a range of numbers starting
from a start value and ending at a bound value incrementing by 1.
- Parameters:
  - Number (`start`): The start value.
  - Number (`bound`): The maximum bound (exclusive).
- Returns - Iterable: An iterable object that returns the range of integers.
- Example:
```kotlin
range(0, 10);
```

### `range(start, bound, step)`
- Description: This is used to generate a range of numbers starting from a
start value and ending at a bound value incrementing by a step value.
- Parameters:
  - Number (`start`): The start value.
  - Number (`bound`): The maximum bound (exclusive).
  - Number (`step`): The step value.
- Returns - Iterable: An iterable object that returns the range of integers.
- Example:
```kotlin
range(0, 10, 2);
```

### `run(path)`
- Description: This is used to run a .arucas file, you can use on script to run other scripts.
- Parameter - String (`path`): As a file path.
- Returns - Object: Any value that the file returns.
- Example:
```kotlin
run('/home/user/script.arucas');
```

### `runFromString(code)`
- Deprecated: This should be replaced with 'eval(code)'
- Description: This is used to evaluate a string as code.
This will not inherit imports that are in the parent script.
- Parameter - String (`code`): The code to run.
- Example:
```kotlin
runFromString('print("Hello World");');
```

### `sleep(milliseconds)`
- Description: This pauses your program for a certain amount of milliseconds.
- Parameter - Number (`milliseconds`): The number of milliseconds to sleep.
- Example:
```kotlin
sleep(1000);
```

### `stop()`
- Description: This is used to stop a script.
- Example:
```kotlin
stop();
```

### `suppressDeprecated(bool)`
- Description: This is used to enable or disable suppressing deprecation warnings.
- Parameter - Boolean (`bool`): True to enable, false to disable warnings.
- Example:
```kotlin
suppressDeprecated(true);
```
## MinecraftExtension

### `hold()`
- Description: This freezes the current thread and halts execution, same functionality as 'Thread.freeze()'.
- Example:
```kotlin
hold();
```
