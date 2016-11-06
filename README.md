# RESTful Jersey-Webserver mit XML

Beispiel fuer einen REST-Server mit Jersey 2.x und XML als Austauschformat.

## Java Architecture for XML Binding (JAXB)

### Annotationen

Zur Uebertragung von Daten in einem HTTP-Request bzw. -Response werden unabhaengige Austauschformate verwendet. Den Vorgang, wenn etwa ein Java-Objekt in ein Austauschformat wie XML oder JSON gewandelt wird, nennt man *Marshalling*. Der entgegengesetzte Vorgang wird *Unmarshalling* genannt.

Im vorliegenden Sourcecode wird XML als Austauschformat verwendet, wobei hierfuer konkret [*JAXB*](https://jaxb.java.net/) Verwendung findet. JAXB definiert eine Menge von Annotationen, die das *Binding* von XML-Elementen an Java-Klassenattribute beschreiben. Im folgenden Auszug aus dem Sourcecode erkennt man einige der Annotationen:

```java
@XmlRootElement(name = "driver")
@XmlAccessorType(XmlAccessType.FIELD)
public class Driver {

    @XmlID
    private String name;
    @XmlElementWrapper(name = "cars")
    @XmlElement(name = "car")
    private List<Car> cars;

    ...

}
```

```java
@XmlRootElement(name = "car")
@XmlAccessorType(XmlAccessType.FIELD)
public class Car {

    private String brand;
    private String model;
    @XmlIDREF
    private Driver driver;

    ...

}
```

* `@XmlAccessorType(XmlAccessType.FIELD)`: Der Accessor-Type beschreibt, wie Elemente der Java-Klasse ermittelt werden, die zum Marshalling herangezogen werden. `XmlAccessType.FIELD` besagt, dass alle nicht-statischen Attribute herangezogen werden sollen.
* `@XmlRootElement(name = "driver")`: Beschreibt das Wurzelelement des XML-Baumes
* `@XmlElement(name = "car")`: Beschreibt ein Element im XML-Baum
* `@XmlElementWrapper(name = "cars")`: Dient dazu, ein zusaetzliches XML-Element zu definieren, in das das annotierte Element eingebettet wird

### Zyklische Referenzen

In den abgebildeten Klassen `Driver` und `Car` ist eine zyklische Referenz zu erkennen: Ein Fahrer faehrt mehrere Autos, waehrend ein Auto immer einem Fahrer zugeordnet ist. Beim Marshalling der Klasse `Driver` wird das Element `cars` in seine XML-Repraesentation gebracht. Dabei wird jedes Element der Liste als Klasse `Car` verarbeitet. Hierbei wird nun wiederum das Element `driver` und die zugehoerige Klasse `Driver` einem Marshalling unterzogen. Es entsteht eine Endlosschleife.

Um diese zyklische Referenz aufzuloesen, bietet sich die Verwendung von XML-Referenzen an. Hierzu dienen die JAXB-Annotationen `@XmlID` und `@XmlIDREF`: 

* `@XmlID`: Definiert ein Element als Identifier
* `@XmlIDREF`: Referenziert einen Identifier

Die Verwendung dieser Referenzen sorgt nun dafuer, dass das Element `driver` in der Klasse `Car` nicht mehr zum vollstaendigen Marshalling der Klasse `Driver` fuehrt, sondern lediglich zu einem Element `driver`, das eine Referenz auf das Element `name` des zugehoerigen Fahrers enthaelt.

## Jersey

[Jersey](https://jersey.java.net/) ist die Referenzimplementierung der [JAX-RS API](https://jax-rs-spec.java.net/) und stellt eine Bibliothek dar, um RESTful Webservices zu entwickeln.

### Services

```java
@Path("drivers")
public class DriverService {

    @POST
    @Path("{name}/cars")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Boolean addCarToDriver(@PathParam("name") String name, Car car) {
        ...
    }

    ...

}
```

* `@Path`: Pfad in der URL, unter der ein Service / eine Resource erreichbar ist
* `@GET`, `@POST`, `@PUT`, `@DELETE`: HTTP-Methoden, unter der eine Resource erreichbar ist
* `@Consumes`: Typ des Eingabe-Austauschformats
* `@Produces`: Typ des Ausgabe-Austauschformats
* `@PathParam`: Mapping auf einen Teil-Pfad innerhalb der URL
* `@QueryParam`: Mapping auf einen Query-Parameter innerhalb der URL

### Server

Ein Jersey REST-Service kann ueber verschiedene Webserver (JDK-HttpServer, Grizzly, Jetty, etc.) angeboten werden. In diesem Beispiel wird der im JDK enthaltene JDK-HttpServer verwendet. Eine Beispielkonfiguration ist nachfolgend zu sehen.

```java
ResourceConfig config = new ResourceConfig();
config.register(JacksonJaxbXMLProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
config.register(DriverService.class);
```

Fuer Marshalling und Unmarshalling von Java-Objekten nach XML wird die Bibliothek *Jackson* verwendet. Hierfuer existiert ein `JacksonJaxbXMLProvider`, der die von JAX-RS vorgesehenen `MessageBodyReader` (Unmarshalling) und `MessageBodyWriter` (Marshalling) implementiert. Dieser wird im Konfigurationsobjekt (`ResourceConfig`) fuer Jersey registiert. Darueberhinaus wird der `DriverService`, also der RESTful-Webservice, registriert.

Anschließend kann mittels dieser Konfiguration der JDK-HttpServer gestartet werden:

```java
URI baseUri = UriBuilder.fromUri("http://localhost").port(55554).build();
JdkHttpServerFactory.createHttpServer(baseUri, config);
``` 