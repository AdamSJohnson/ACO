# ACO
This is an ant colony optimization implementation written for an AI class at SUNY Potsdam.

The code is mostly undocumented and worked to achieve the goal presented in the assignment.

Hopefully you find this helpful.

# Data File Format

Looking at the data file you will see entries entered as such:
```
Blue_Mountains
1250
4
Lake_Evendim 250 0 41 A
Michel_Delving 270 65 15 B
White_Towers 225 75 21 C
Grey_Havens 240 75 21 D
```

So what do you have there?
```
{Node Name}
{Hueristic Value to Goal}
{Number of Connected cities}
{Connected_City_Name} {Distance to City} {Road Condition} {Danger Level} {Path Key}
```

Some values like Road Condition and Danger level can be used in your hueristics function. This was information provided in the assignment
The final city AKA the goal is the last entry and is formatted as follows:

```
{City Name}
0
0
```
