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
