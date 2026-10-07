import it.kamaladafrica.codicefiscale.City;
import it.kamaladafrica.codicefiscale.CodiceFiscale;
import it.kamaladafrica.codicefiscale.Person;
import it.kamaladafrica.codicefiscale.city.CityByName;
import it.kamaladafrica.codicefiscale.city.CityProvider;


private static final Scanner scanner = new Scanner(System.in);

// Metodo di utilità per richiedere input testuali da console
public static String readln(String prompt) {
    System.out.print(prompt + " ");
    return scanner.nextLine();
}

void main() throws IOException {
//    Faker faker = new Faker();
//    println(faker.chuckNorris().fact());
//    println(faker.yoda().quote());
//
//
//    String pokemon = faker.pokemon().name();
//
//    println("E' apparso un pokemon selvatico: " + pokemon);
//
//    println(FigletFont.convertOneLine(pokemon));
//
//    File temp = QRCode.from("https://www.pokemon.com/it/pokedex/" + pokemon.toLowerCase())
//            .to(ImageType.PNG)
//            .withSize(300,300)
//            .file();
//
//    File dest = new File("QRCODE" + pokemon + ".png");
//    Files.copy(temp.toPath(), dest.toPath());

    // Inizializzazione del provider predefinito per la ricerca dei comuni (Belfiore)
    CityByName cities = CityProvider.ofDefault();

    System.out.println("--- Inserimento Dati Persona ---");

    // Acquisizione dinamica dei dati
    String firstname = readln("Inserisci il nome:");
    String lastname = readln("Inserisci il cognome:");

    System.out.print("Inserisci la data di nascita (Formato AAAA-MM-DD, es. 1975-03-22): ");
    LocalDate birthDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

    System.out.print("È femmina? (true/false): ");
    boolean isFemale = Boolean.parseBoolean(scanner.nextLine());

    String cityName = readln("Inserisci la citta:");
    City city = cities.findByName(cityName);

    if (city == null) {
        System.err.println("Errore: Città non trovata nel database Belfiore!");
        return;
    }

    // Costruzione dell'oggetto Person tramite il builder della libreria
    Person person = Person.builder()
            .firstname(firstname)
            .lastname(lastname)
            .birthDate(birthDate)
            .isFemale(isFemale)
            .city(city)
            .build();

    System.out.println("\n--- Risultati Codice Fiscale ---");

    // 1. Codice fiscale standard (livello omocodia 0)
    CodiceFiscale cf0 = CodiceFiscale.of(person);
    System.out.println("Valore standard: " + cf0.getValue());             // Es: RSSMRA75C22H501I
    System.out.println("Livello omocodia: " + cf0.getOmocodeLevel());       // 0

    // 2. Codice fiscale forzato a livello di omocodia 3
    CodiceFiscale cf2 = CodiceFiscale.of(person).toOmocodeLevel(3);
    System.out.println("Valore omocodificato: " + cf2.getValue());        // Es: RSSMRA75C22H5LML
    System.out.println("Livello omocodia: " + cf2.getOmocodeLevel());       // 3

    // 3. Confronto tra codici fiscali con livelli di omocodia differenti
    System.out.println("I due codici sono uguali? " + cf0.isEqual(cf2));    // true (ignora il livello)

    // 4. Normalizzazione (riporta il codice allo stato base / livello 0)
    CodiceFiscale cf = cf2.normalized();
    System.out.println("Valore normalizzato: " + cf.getValue());            // Es: RSSMRA75C22H501I
    System.out.println("Livello omocodia normalizzato: " + cf.getOmocodeLevel()); // 0
}