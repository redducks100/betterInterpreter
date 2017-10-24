# betterInterpreter - COMP1202 - Space Cadets Challenge Week 3
A better interpreter for barebones.

## Added features:
  * *var* declaration (added *string* support): 
      ```
         var a;
         var a = " ";
         var a = 10;
      ```
  * *expressions*:
      ```
         var a;
         a = (5.2+1.3)/3.5;
         ```
  * *printing* variables or *expressions*:
      ```
         print a;
         print (5+6);
      ```
  * *if*, *while* and *for* statements:
      ```
         if cond then expr; else expr;
         while cond then expr;
         for init;cond;incr then expr;
      ```
  * *login* operators:
     ```
     not (!=), equals (==), less (<), less_equals (<=), greater (>), great_equals (>=), or (||), and (&&)
     ```
  * *true* and *false* constants;
    
