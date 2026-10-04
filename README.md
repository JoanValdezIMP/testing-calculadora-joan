# Activitat 2 - Unit Testing, JaCoCo, PIT i Mockito - Joan Valedz

Aquest repositori conté la solució a la pràctica de proves unitàries: disseny de casos límit, parametrització, anàlisi de cobertura i avaluació de mutants.

## Instruccions d'execució

Per validar el projecte, pots utilitzar les següents comandes de Maven a la terminal:

- **Executar la suite de tests:** `mvn test`
- **Generar l'informe de cobertura (JaCoCo):** `mvn clean test jacoco:report`
  *(L'informe es genera a `target/site/jacoco/index.html`)*
- **Executar Mutation Testing (PIT):** `mvn org.pitest:pitest-maven:mutationCoverage`
  *(L'informe es genera a `target/pit-reports/index.html`)*

---------------------------------------------------------------------------------------

## Part A - Calculator

**Evidència d'execució (`mvn test`):**
Captura de l'execució dels tests de Calculator: 
![Captura test correcte](./img/tests-successful.png)

---------------------------------------------------------------------------------------

## Part B - DescompteService

S'han resolt els casos de prova de funcionament normal, valors límit i aplicació de descomptes mitjançant un test parametritzat (`@CsvSource`) per mantenir el codi net i llegible, deixant únicament el cas d'excepció (import negatiu) en un test separat (`@Test`).

**Combinacions addicionals identificades i justificades:**
A més dels casos mínims demanats a l'enunciat, s'han identificat i afegit dues combinacions rellevants al test parametritzat:

1. **Client Premium amb import inferior a 100 (ex: `50.0, true`):** L'he afegit perquè l'enunciat no cobria el cas d'un client Premium que no assoleix l'import mínim. Això és necessari per cobrir completament la branca lògica de la condició i comprovar que no se li aplica el descompte del 20%.
2. **Límit exacte de no-negativitat (`0.0, false`):** L'he afegit per assegurar que una compra de valor zero retorna `0.0` sense llançar cap excepció. Aquesta combinació va resultar fonamental per matar un mutant de PIT que alterava la condició d'import negatiu.

---------------------------------------------------------------------------------------

## Part C - Cobertura amb JaCoCo

**1. Quines línies no estan cobertes?**
Inicialment faltava cobrir l'excepció de `potencia` i la divisió `normal` a Calculator, i l'excepció de `null` a ComandaService. Amb els nous tests, la cobertura actual és del 100%.

**2. Quines branques de DescompteService no s'han executat?**
Faltava executar la branca on un client és Premium, però l'import no arriba a 100 (per exemple, `clientPremium = true` i `importCompra = 50.0`).

**3. Quin test afegiries per cobrir una branca pendent?**
S'ha afegit el cas `"50.0, true, 50.0"` al test parametritzat per cobrir la situació del client Premium sense descompte per no arribar a l'import mínim.

**4. La cobertura obtinguda et sembla suficient? Justifica-ho.**
Sí. S'ha assolit un 100% en línies i branques, però la suficiència recau en el fet que els tests s'han dissenyat tenint en compte valors límit (0.0, 99.99, 100.0) i casos d'error, assegurant que validem les regles de negoci i no només forçant el pas per les línies de codi.

**Captura d'informe JaCoCo:** 
![Captura cobertura JaCoCo](./img/cobertura-jacoco.png)

---------------------------------------------------------------------------------------

## Part D - Mutation Testing amb PIT (Anàlisi de Mutants)

Taula dels mutants supervivents inicialment i resolució per obtenir el 100% de Mutation Score:

| Classe i Línia | Mutant introduït per PIT | Estat | Per què ha sobreviscut? (Basat en els tests) | Acció per matar-lo |
| :--- | :--- | :--- | :--- | :--- |
| `Calculator` (Línia 14) | *Replaced int return with 0 for... multiplica* | Survived | PIT ha canviat el codi intern perquè la multiplicació retorni sempre `0`. Ha sobreviscut perquè l'únic test que teníem per a aquesta funció era `5 * 0`, que espera `0` com a resultat. Com que el test esperava `0` i el mutant retorna `0`, el test ha passat en verd sense adonar-se de la trampa. | Afegir un test de multiplicació amb valors diferents de zero (ex: `3 * 5 = 15`). |
| `DescompteService` (Línia 7) | *Changed conditional boundary* | Survived | PIT ha canviat la condició `importCompra < 0` per `importCompra <= 0`. Ha sobreviscut perquè teníem un test per a `-1` (que llança excepció) i un per a `50` (cas normal), però no teníem cap test per al valor límit exacte `0.0`. | Afegir el valor límit `0.0` al test parametritzat esperant que retorni `0.0` sense llançar excepció. |

**Comparació Abans / Després i Millora del Test:**
Seguint l'objectiu de la Part D, he millorat la suite de proves per resoldre les debilitats detectades pels mutants supervivents. 

* **Abans:** L'informe inicial de PIT mostrava un **94% de Mutation Coverage** amb 2 mutants supervivents.
* **Millora aplicada:** Per matar aquests mutants, s'ha afegit el següent codi a les classes de test:
  * A `CalculatorTest.java`: `assertEquals(15, calc.multiplica(3, 5));` (per matar el mutant que retornava 0).
  * A `DescompteServiceTest.java`: S'ha afegit el cas límit `"0.0, false, 0.0"` al `@CsvSource` (per matar el mutant de canvi de condició de límit).
* **Després:** En tornar a executar PIT, els mutants han estat eliminats (Killed) i la suite ha assolit un **100% de Mutation Coverage**.

**Evidències de l'informe PIT:**

Captura d'informe PIT **Abans** de les millores: 
![Captura PIT Abans](./img/pit-unsuccessful.png)

Captura d'informe PIT **Després** de les millores: 
![Captura PIT Despres](./img/pit-successful.png)

---------------------------------------------------------------------------------------

## Preguntes finals de reflexió

**1. Quina diferència has observat entre que una línia estigui coberta i que el comportament estigui ben provat?**
Que una línia estigui coberta només vol dir que l'execució hi ha passat. Un comportament ben provat requereix assecions robustes; JaCoCo donava 100% amb `5 * 0 = 0`, però no provàvem si la multiplicació funcionava amb altres nombres fins que PIT ho va revelar.

**2. Què t'ha aportat PIT que no t'havia mostrat l'informe de JaCoCo?**
JaCoCo mesura la quantitat de codi executat, mentre que PIT n'avalua la qualitat i resiliència. PIT ha demostrat que tenir un 100% de cobertura pot amagar assecions febles o valors límit oblidats.

**3. Quin valor límit ha estat més rellevant en DescompteService i per què?**
Principalment el `0.0` (vital per detectar el mutant a la condició d'import negatiu) i el `100.0` (frontera exacta on canvien les regles de negoci i comencen a aplicar-se els descomptes).

**4. En quin cas t'ha resultat útil el mock de StockRepository?**
Ha estat fonamental per provar `ComandaService` de forma totalment aïllada. Ens ha permès forçar respostes de l'inventari (`true`/`false`) sense dependre d'una connexió real a base de dades, garantint tests ràpids i deterministes.

**5. Quin test de la teva suite consideres que aporta més valor i per què?**
El test parametritzat de `DescompteService`. Aporta molt valor perquè permet validar múltiples escenaris (valors normals, límits inferiors, fronteres exactes i combinacions de client Premium) reciclant la mateixa asseció, mantenint el codi net i llegible.




--// Anunciat //--

# Activitat 2 - Unit Testing, Cobertura, Mutation Testing i Mockito

Aquest projecte serveix com a plantilla base (*starter*) per a la pràctica de proves unitàries a Java. L'objectiu principal és aprendre i aplicar les tècniques fonamentals de verificació de programari: disseny de casos de prova amb **JUnit 5**, mesura de cobertura de codi amb **JaCoCo**, avaluació de la qualitat dels tests mitjançant **Mutation Testing (PIT)** i aïllament de dependències amb **Mockito**.

---

## 🎯 Objectius d'aprenentatge

1. **JUnit 5 bàsic i parametritzat**:
   - Escriure tests unitaris assertius (`assertEquals`, `assertTrue`, `assertThrows`, etc.).
   - Utilitzar proves parametritzades (`@ParameterizedTest`, `@CsvSource`, `@ValueSource`) per evitar duplicació de codi de test.
2. **Mesura i anàlisi de cobertura (JaCoCo)**:
   - Comprendre la cobertura de línia (*Line Coverage*) i de branca (*Branch Coverage*).
   - Identificar camins d'execució no coberts i condicions límit.
3. **Mutation Testing (PIT)**:
   - Entendre el concepte de *mutant* (canvis sintàctics automàtics al codi font) i com avaluar l'eficàcia dels tests per "matar" aquests mutants (*Mutation Score*).
4. **Dobles de prova i aïllament amb Mockito**:
   - Aprendre a aïllar una classe de les seves dependències externes mitjançant *mocks*.
   - Configurar respostes fictícies (*stubbing* amb `when(...).thenReturn(...)`).
   - Verificar interaccions i comportaments (`verify(...)`, `verifyNoInteractions(...)`).

---

## 📂 Estructura del projecte

```text
activitat2-unit-testing/
├── pom.xml                                    # Configuració Maven (JUnit 5, Mockito, JaCoCo, PIT)
├── .gitignore                                 # Fitxers exclosos de Git (target, IDEs, etc.)
├── README.md                                  # Guia del projecte
└── src/
    ├── main/java/cat/uvic/testing/            # Classes de negoci (producció)
    │   ├── Calculator.java                    # Lògica bàsica per a proves unitàries i parametritzades
    │   ├── DescompteService.java              # Lògica condicional per a cobertura de branques i PIT
    │   ├── StockRepository.java               # Interfície de dependència (accés a dades d'estoc)
    │   └── ComandaService.java                # Servei amb dependència injectable per a Mockito
    └── test/java/cat/uvic/testing/            # Paquet on l'alumnat implementarà els tests
```

---

## 📦 Classes del domini

### 1. `Calculator.java`
Conté operacions matemàtiques bàsiques (`suma`, `resta`, `multiplica`, `divideix` i `potencia`).
* **Aspectes a testejar:**
  * Resultats correctes per a valors positius, negatius i zero.
  * Llançament d'excepcions (`IllegalArgumentException`) en dividir per zero o en demanar potències amb exponent negatiu.
  * Ús de proves parametritzades (`@ParameterizedTest`).

### 2. `DescompteService.java`
Calcula el preu final aplicant regles de descompte segons l'import de la compra i si el client és *Premium* (20% per a compres $\ge 100$, 10% per a compres estàndard $\ge 100$, 0% per a imports $< 100$).
* **Aspectes a testejar:**
  * Validació d'imports invàlids (negatius).
  * Anàlisi de valors límit (per exemple: `99.99`, `100.0`, `100.01`).
  * Cobertura del 100% de branques i condicions booleanes compostes.

### 3. `StockRepository.java` & `ComandaService.java`
Representen una arquitectura desacoblada: `ComandaService` necessita comprovar si hi ha estoc d'un producte abans de permetre la compra, delegant aquesta comprovació a `StockRepository`.
* **Aspectes a testejar amb Mockito:**
  * Crear un mock de `StockRepository` i injectar-lo a `ComandaService`.
  * Simular retorn `true` / `false` del repositori i verificar el comportament de `potComprar`.
  * Comprovar que si el producte és `null` o buit, es llança `IllegalArgumentException` i **no es crida mai** al repositori (`verifyNoInteractions`).
  * Verificar que es fa exactament la crida esperada al repositori (`verify`).

---

## 📝 Tasques a realitzar per l'alumnat

L'alumnat ha de crear els següents fitxers dins de `src/test/java/cat/uvic/testing/`:

1. **`CalculatorTest.java`**: Tests unitaris i parametritzats per a totes les operacions i excepcions de `Calculator`.
2. **`DescompteServiceTest.java`**: Bateria de tests per cobrir tots els camins de decisió i resistir el mutation testing de `DescompteService`.
3. **`ComandaServiceTest.java`**: Tests amb Mockito utilitzant `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, `when()` i `verify()`.

---

## 🚀 Comandes d'execució

### 1. Executar els tests unitaris
```bash
mvn test
```

### 2. Executar tests i generar l'informe de cobertura JaCoCo
```bash
mvn clean test
```
* **Ubicació de l'informe:** Obre al navegador el fitxer:
  ```text
  target/site/jacoco/index.html
  ```

### 3. Executar Mutation Testing amb PIT
```bash
mvn org.pitest:pitest-maven:mutationCoverage
```
* **Ubicació de l'informe:** Obre l'últim informe generat a la carpeta:
  ```text
  target/pit-reports/YYYYMMDDHHMM/index.html
  ```

---

## 💻 Requisits de l'entorn

* **Java JDK:** 17 o superior.
* **Apache Maven:** 3.8 o superior.
* **IDE recomanat:** IntelliJ IDEA, Eclipse o Visual Studio Code (amb l'extensió *Extension Pack for Java*).
