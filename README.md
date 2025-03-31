Zadania praktyczne do wykonania znajduja się w archiwum zadania.zip

# Iteratory

## Zadanie 1: Implementacja własnego iteratora

**Wymagania:**
- Stwórz klasę `EvenNumbersIterator`, która iteruje tylko po **parzystych liczbach** z listy liczb całkowitych.
- Klasa powinna implementować `Iterator<Integer>`.
- Zaimplementuj metody `hasNext()` oraz `next()`.
- Ignoruj liczby nieparzyste.

**Przykładowe użycie:**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
EvenNumbersIterator iterator = new EvenNumbersIterator(numbers);

while (iterator.hasNext()) {
    System.out.println(iterator.next());  // Powinno wypisać: 2, 4, 6, 8
}
```

---

## Zadanie 2: Własna implementacja `Iterable`

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

## Zadanie 3: Iterator liczb Fibonacciego

**Wymagania:**
- Napisz klasę `FibonacciIterator`, która zwraca kolejne liczby Fibonacciego.
- Klasa powinna implementować `Iterable<Integer>`.
- Każde wywołanie `next()` zwraca kolejną liczbę Fibonacciego.

**Przykładowe użycie:**
```java
Iterator<Integer> it = new FibonacciIterator(10).iterator();
while (it.hasNext()) {
    System.out.println(it.next());  
}
// Powinno wypisać: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
```
---

## Zadanie 4: Iterator do czytania pliku JSON

**Wymagania:**
- Napisz klasę `JsonIterator`, która zwraca zawartosci kolejnych obiektów z pliku data_zad3.json.
- Klasa powinna implementować `Iterable<String>`.
- Użyj ```gson.fromJson(reader, new TypeToken<List<Person>>() {}.getType())``` do odczytania listy obiektów z pliku.

**Przykładowe użycie:**
```java
Iterator<String> it = new JsonIterator(jsonFilePath.getPath()).iterator();
while(it.hasNext()) {
    System.out.println(it.next());  // Powinno wypisać: "name: exampleName age: exampleAge city: exampleCity"
}
```
# Generatory

## Zadanie 5: Generator liczb Fibonacciego

**Wymagania:**

- Napisz klasę `FibonacciGenerator` która generuje kolejne liczby Fibonacciego pojedynczo.
- Klasa powinna implementować `Iterable<Integer>`

```java
FibonacciGenerator generator = new FibonacciGenerator();
Stream.iterate(generator.iterator(), it -> it)
        .map(it -> it.next())
        .limit(10)
        .forEach(System.out::println);  // Powinno wypisać: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
```

---

## Zadanie 6: Użycie generatora do odczytu dużych plików

**Wymagania**

- Napisz metode `lazyJSONLoader` która zwraca `Stream<String>`.
- Używając klasy jsonReader odczytaj JSON Array obiekt po obiekcie bez wczytywania całego pliku na raz.
- Na koniec możesz zmienić odczytywany plik na data_zad6_large.json by sprawdzić czy faktycznie plik nie jest wczytywany w całości.

```java
Stream<String> stream = tmp.lazyJSONLoader(jsonFilePath.getPath());
stream.forEach(System.out::println);  // Powinno wypisać: "name: exampleName age: exampleAge city: exampleCity"
```