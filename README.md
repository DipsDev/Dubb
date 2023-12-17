
# Dubb
A simple custom interpeter and a programming language





## Features

### Variables
```javascript
final var favorite_number = 49; // this is a constant variable
var age = 17; // this is not!
```

### Functions
Dubb supports if statements using the `if` keyword, \
and initializes function using the `func` keyword.

Note: that there are no `()` in the if statement.
```javascript
func max(a, b) {
    if a > b { // if statement
        return a;
    }
    if a <= b { // another if statement
        return b;
    }
}
print(max(3, 7));
```

#### Global Functions
There are currently two global functions.
```javascript
pow(x: int, y: int) == x^y // returns x to the power of y
print(x: any) // prints out anything to the screen
```




### Math Equations
Dubb supports basic math equations and follows the normal priority
```
final var y = 2;
var x = 4 + 5 * pow(y, 2) - 4 / 2;
print(x); // prints out 22
```



## Roadmap

- ~~If statements~~

- Loops - while, for, foreach

