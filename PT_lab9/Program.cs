using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;
using System.Xml.Linq;
using System.Xml.Serialization;
using System.Xml.XPath;



public class Engine
{
    [XmlAttribute("model")]
    public string model { get; set; }
    public double displacement { get; set; }
    public double horsePower { get; set; }

    public Engine() { }

    public Engine(double displacement, double horsePower, string model)
    {
        this.displacement = displacement;
        this.horsePower = horsePower;
        this.model = model;
    }
}

/*[XmlRoot("car")]*/
public class Car
{
    public string model { get; set; }
    public int year { get; set; }
    [XmlElement("engine")]
    public Engine motor { get; set; }

    
    public Car() { }

    public Car(string model, Engine motor, int year)
    {
        this.model = model;
        this.year = year;
        this.motor = motor;
    }
}

class Program
{
    static void Main(string[] args)
    {
        List<Car> myCars = new List<Car>()
        {
            new Car("E250", new Engine(1.8, 204, "CGI"), 2009),
            new Car("E350", new Engine(3.5, 292, "CGI"), 2009),
            new Car("A6", new Engine(2.5, 187, "FSI"), 2012),
            new Car("A6", new Engine(2.8, 220, "FSI"), 2012),
            new Car("A6", new Engine(3.0, 295, "TFSI"), 2012),
            new Car("A6", new Engine(2.0, 175, "TDI"), 2011),
            new Car("A6", new Engine(3.0, 309, "TDI"), 2011),
            new Car("S6", new Engine(4.0, 414, "TFSI"), 2012),
            new Car("S8", new Engine(4.0, 513, "TFSI"), 2012)
        };

        var query1 = myCars
            .Where(car => car.model == "A6")
            .Select(car => new
            {
                engineType = car.motor.model == "TDI" ? "diesel" : "petrol",
                hppl = (double)car.motor.horsePower / car.motor.displacement
            });

        var query2 = from car in query1
                     group car by car.engineType into engineGroup
                     select new
                     {
                         engineType = engineGroup.Key,
                         averageHppl = engineGroup.Average(car => car.hppl)
                     };

        foreach(var group in query2)
        {
            Console.WriteLine($"{group.engineType}:{group.averageHppl}");
        }


        // ZADANIE 2 i 3

        // Serializacja
        XmlSerializer serializer = new XmlSerializer(typeof(List<Car>), new XmlRootAttribute("cars"));
        XmlSerializerNamespaces ns = new XmlSerializerNamespaces();
        ns.Add("", "");


        using (XmlWriter writer = XmlWriter.Create("myCars.xml"))
        {
            serializer.Serialize(writer, myCars, ns);
    
        }

        XElement rootNode = XElement.Load("myCars.xml");


        string filePath = "myCars.xml";

        // Załaduj dokument XML
        XDocument doc1 = XDocument.Load(filePath);

        // Przeprowadź modyfikację
        foreach (var car in doc1.Descendants("Car"))
        {
            car.Name = "car";
        }

        // Zapisz zmieniony dokument XML
        doc1.Save(filePath);



        double avgHP = CalculateAverageHorsePower(rootNode);
        Console.WriteLine($"Przeciętna moc samochodów o silnikach innych niż TDI: {avgHP}");

        IEnumerable<string> models = GetUniqueCarModels(rootNode);
        Console.WriteLine("\nModele samochodów bez powtórzeń:");
        foreach (var model in models)
        {
            Console.WriteLine(model);
        }


        // Deserializacja
        List<Car> deserializedCars;
        using (XmlReader reader = XmlReader.Create("myCars.xml"))
        {
            deserializedCars = (List<Car>)serializer.Deserialize(reader);
        }
        

        // ZADANIE 4
        createXmlFromLinq(myCars, serializer);

        // ZADANIE 5

        // Wczytaj dane z XML
        var xml = XElement.Load("CarsFromLinq.xml");

        // Wygeneruj tabelę XHTML
        var table = new XElement("table",
                        new XElement("tr",
                            xml.Elements("Car").First().Elements().Select(e => new XElement("th", e.Name.LocalName))),
                        xml.Elements("Car").Select(car =>
                            new XElement("tr",
                                car.Elements().Select(e => new XElement("td", e.Value)))));

        // Utwórz nowy dokument XHTML
        var xhtmlDocument = new XDocument(
                                new XElement("html",
                                    new XElement("head",
                                        new XElement("title", "Cars Data")),
                                    new XElement("body",
                                        table)));

        // Wczytaj szablon XHTML
        var template = XElement.Load("template.html");

        // Znajdź w szablonie element o id="content" i zastąp go wygenerowanym dokumentem
        var content = template.Descendants().FirstOrDefault(e => e.Attribute("id")?.Value == "content");
        content.ReplaceWith(xhtmlDocument.Root);

        // Zapisz zmodyfikowany szablon do pliku
        var outputPath = "output.html";
        File.WriteAllText(outputPath, template.ToString());

        Console.WriteLine($"Dokument został zapisany jako '{outputPath}'");


        // ZADANIE 6

        // Ścieżka do pliku XML
       
        var outputPath1 = "modifiedMyCars.xml";

        // Załaduj dokument XML
        XDocument doc = XDocument.Load(filePath);

        // Przeprowadź modyfikację
        foreach (var car in doc.Descendants("car"))
        {
            // Zmiana nazwy elementu horsePower na hp
            var horsePowerElement = car.Element("engine").Element("horsePower");
            if (horsePowerElement != null)
            {
                horsePowerElement.Name = "hp";
            }

            // Zamiast elementu year utwórz atrybut o tej samej nazwie w elemencie model
            var yearElement = car.Element("year");
            if (yearElement != null)
            {
                string yearValue = yearElement.Value;
                car.Element("model").SetAttributeValue("year", yearValue);
                yearElement.Remove();
            }
        }

        // Zapisz zmieniony dokument XML
        doc.Save(outputPath1);
    }

   

    private static void createXmlFromLinq(List<Car> myCars, XmlSerializer serializer)
    {
        IEnumerable<XElement> nodes = from car in myCars
                                      select new XElement("Car",
                                                 new XElement("model", car.model),
                                                 new XElement("year", car.year),
                                                 new XElement("motor",
                                                     new XAttribute("model", car.motor.model),
                                                     new XElement("displacement", car.motor.displacement),
                                                     new XElement("horsePower", car.motor.horsePower)
                                                 )
                                             );

        XElement rootNode = new XElement("cars", nodes);
        rootNode.Save("CarsFromLinq.xml");
    }

    static double CalculateAverageHorsePower(XElement rootNode)
    {
        // Wyrażenie XPath do znalezienia samochodów o silnikach różnych od TDI
        string xpathExpression = "//Car[not(motor/@model='TDI')]/motor/horsePower";
        // Wybór węzłów reprezentujących moc silników
        IEnumerable<XElement> horsePowerNodes = rootNode.XPathSelectElements(xpathExpression);

        // Obliczenie sumy mocy silników
        double totalHP = horsePowerNodes.Sum(node => (double)node);
        // Obliczenie średniej mocy
        double avgHP = totalHP / horsePowerNodes.Count();
        return avgHP;
    }

    static IEnumerable<string> GetUniqueCarModels(XElement rootNode)
    {
        // Wyrażenie XPath do znalezienia unikalnych modeli samochodów
        string xpathExpression = "//Car/model[not(. = following::model)]";
        // Wybór węzłów reprezentujących modele samochodów
        IEnumerable<XElement> modelNodes = rootNode.XPathSelectElements(xpathExpression);

        // Zwrócenie nazw modeli jako ciągów znaków
        return modelNodes.Select(node => node.Value);
    }
}