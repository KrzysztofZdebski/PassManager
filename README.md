Zadania praktyczne do wykonania znajduja się w archiwum zadania.zip

# Zadania: Iterator w Javie

## Zadanie 1: Implementacja własnego iteratora
**Cel:** Nauczyć się implementacji własnego iteratora dla niestandardowej kolekcji.

**Wymagania:**
- Stwórz klasę `EvenNumbersIterator`, która iteruje tylko po **parzystych liczbach** z listy liczb całkowitych.
- Klasa powinna implementować `Iterator<Integer>`.
- Zaimplementuj metody `hasNext()` oraz `next()`.
- Ignoruj liczby nieparzyste.

**Przykładowe użycie:**
```java
List<Integer> numbers = Arrays.asList(1, 1, 2, 3, 4, 5, 6, 6, 7, 8);
EvenNumbersIterator iterator = new EvenNumbersIterator(numbers);

while (iterator.hasNext()) {
    System.out.println(iterator.next());  // Powinno wypisać: 2, 4, 6, 6, 8
}
```

---

## Zadanie 2: Własna implementacja `Iterable`
**Cel:** Zrozumienie różnicy między `Iterator` a `Iterable`.

**Wymagania:**
- Napisz klasę `Range`, która implementuje `Iterable<Integer>` i zwraca liczby w zakresie `[start, end)`.
- Klasa powinna zwracać iterator przechodzący przez liczby w podanym zakresie.

**Przykładowe użycie:**
```java
for (int i : new Range(5, 10)) {
    System.out.println(i);  // Powinno wypisać: 5, 6, 7, 8, 9
}
```

---

## Zadanie 3: Usuwanie elementów z listy za pomocą iteratora
**Cel:** Nauczyć się korzystania z metody `remove()` w iteratorze.

**Wymagania:**
- Napisz metodę `removeOddNumbers(List<Integer> list)`, która usuwa wszystkie liczby nieparzyste z listy, używając `Iterator.remove()`.

**Przykładowe użycie:**
```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
removeOddNumbers(numbers);
System.out.println(numbers);  // Powinno wypisać: [2, 4, 6]
```

---

## Zadanie 4: Iterator w `HashMap`
**Cel:** Nauczyć się iteracji po `Map`.

**Wymagania:**
- Napisz metodę, która iteruje po `HashMap<String, Integer>` i wypisuje tylko te wpisy, w których wartość jest większa niż 50.

**Przykładowe użycie:**
```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 70);
scores.put("Bob", 40);
scores.put("Charlie", 90);
filterAndPrint(scores);
// Powinno wypisać:
// Alice - 70
// Charlie - 90
```

---

## Zadanie 5: Iterator nieskończony
**Cel:** Zrozumienie idei iteratorów generujących wartości dynamicznie.

**Wymagania:**
- Napisz klasę `FibonacciIterator`, która generuje kolejne liczby Fibonacciego.
- Klasa powinna implementować `Iterator<Integer>`.
- Każde wywołanie `next()` zwraca kolejną liczbę Fibonacciego.

**Przykładowe użycie:**
```java
FibonacciIterator it = new FibonacciIterator();
for (int i = 0; i < 10; i++) {
    System.out.println(it.next());  
}
// Powinno wypisać: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
```

