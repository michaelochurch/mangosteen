# mangosteen

A library for interactive exploration of Clojure data. 

## Usage

Sometimes you have a gnarly piece of data and want to explore it. This
can be done at the REPL, but navigation and exploration of the tree
end up looking like this.

```
user> my-data
(let [x (rand-int 6) y (rand-int 10)] (if (< x y) x (+ y x)))
user> (nth *1 1)
[x (rand-int 6) y (rand-int 10)]
user> (class *1)
clojure.lang.PersistentVector
user> (nth *2 2)
y
user> (class *1)
clojure.lang.Symbol
```

That's far from ideal. In many cases, you want a more idiomatic way of
walking a data structure-- without leaving the REPL. That's what
Mangosteen, an interactive wrapper around clojure.zip, is for.

### Exploration

Use the function ```explore``` to navigate your data structure, like so.

```
user=> (use 'mangosteen.core)
nil
user=> (def my-data '(defn f [x] (if (= (mod x 2) 0) (quot x 2) (+ (* x 3) 1))))
#'user/my-data
user=> (explore my-data)

---------------------------------------------------------------------------
CURRENT FORM:
(defn f [x] (if (= (mod x 2) 0) (quot x 2) (+ (* x 3) 1)))
... of class clojure.lang.PersistentList

0: defn
1: f
2: [x]
3: (if (= (mod x 2) 0) (quot x 2) (+ (* x 3) 1))

(s)elect, (q)uit, (u)p to parent, or (0, 1, ...) to descend.
Your choice? 3


---------------------------------------------------------------------------
CURRENT FORM:
(if (= (mod x 2) 0) (quot x 2) (+ (* x 3) 1))
... of class clojure.lang.PersistentList

... parent is: (defn f [x] (if (= (mod x 2) 0) (quot x 2) (+ (* x 3) 1)))

0: if
1: (= (mod x 2) 0)
2: (quot x 2)
3: (+ (* x 3) 1)

(s)elect, (q)uit, (u)p to parent, or (0, 1, ...) to descend.
Your choice? 1


---------------------------------------------------------------------------
CURRENT FORM:
(= (mod x 2) 0)
... of class clojure.lang.PersistentList

... parent is: (if (= (mod x 2) 0) (quot x 2) (+ (* x 3) 1))

0: =
1: (mod x 2)
2: 0

(s)elect, (q)uit, (u)p to parent, or (0, 1, ...) to descend.
Your choice? s

[:select (= (mod x 2) 0)]
```

If (s)elect is used, the exploration terminates and the subform is
returned in ```[:select <subform>]```. If (q)uit is used, then ```[:quit]``` is 
returned.

### "Modification"

Often it's desirable to "modify" a nested data structure. (I use
quotes because the original is unchanged: a modified copy is
returned.)

In modification, the function `interact` is used. (S)elect is used to
choose a node for modification, and (q)uit is used to stop making
edits. The modified copy of the data (with all edits) will be
returned.

```

user=> (def my-data [[1 2] [3 nil]])
#'user/my-data
user=> (interact my-data)

---------------------------------------------------------------------------
CURRENT FORM:
[[1 2] [3 nil]]
... of class clojure.lang.PersistentVector

0: [1 2]
1: [3 nil]

(s)elect, (q)uit, (u)p to parent, or (0, 1, ...) to descend.
Your choice? 1


---------------------------------------------------------------------------
CURRENT FORM:
[3 nil]
... of class clojure.lang.PersistentVector

... parent is: [[1 2] [3 nil]]

0: 3
1: nil

(s)elect, (q)uit, (u)p to parent, or (0, 1, ...) to descend.
Your choice? 1


---------------------------------------------------------------------------
CURRENT FORM:
nil
... of class nil

... parent is: [3 nil]

... is atomic

(s)elect, (q)uit, (u)p to parent, or (0, 1, ...) to descend.
Your choice? s

Edit to what? 4


---------------------------------------------------------------------------
CURRENT FORM:
4
... of class java.lang.Long

... parent is: [3 4]

... is atomic

(s)elect, (q)uit, (u)p to parent, or (0, 1, ...) to descend.
Your choice? q

[[1 2] [3 4]]
```

## License

Copyright © 2014 Michael O. Church.
Distributed under the MIT License. 