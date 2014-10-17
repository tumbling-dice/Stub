using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;
using Sgml;
using System.IO;

namespace SpellCardExtractor
{
    class Program
    {
        private const string BASE_URL = "http://thwiki.info/?%A5%AD%A5%E3%A5%E9%CA%CC%A5%B9%A5%DA%A5%EB%A5%AB%A1%BC%A5%C9%2F";
        private const int MAX_PAGE = 4;
        private const string OUTPUT_FILE_PATH = @"C:\spells.xml";

        private static int _charaId = 0;
        private static int _spellId = 0;

        static void Main(string[] args)
        {
            var charaList = new List<Chara>();
            for (int i = 1; i <= MAX_PAGE; i++)
            {
                var url = string.Format("{0}{1}", BASE_URL, i);
                charaList.AddRange(Scrape(url));
            }

            var doc = new XElement("Characters", charaList.Select(x => x.ToXml()));
            doc.Save(OUTPUT_FILE_PATH);
        }

        static List<Chara> Scrape(string url)
        {
            XDocument xml;
            using (var sgml = new SgmlReader() { Href = url, IgnoreDtd = true })
            {
                xml = XDocument.Load(sgml);
            }

            var ns = xml.Root.Name.Namespace;
            var items = xml.Descendants(ns + "body")
                           .Descendants()
                           .Where(x => x.Name == ns + "h4" || (x.Name == ns + "table" && (string)x.Attribute("class") == "style_table"));

            var charaList = new List<Chara>();

            Chara chara = null;
            List<SpellCard> spellCardList = null;

            _charaId += -4;

            foreach (var e in items)
            {
                if (e.Name == ns + "h4")
                {
                    if (chara != null && spellCardList != null)
                    {
                        chara.SpellCards = spellCardList;
                        charaList.Add(chara);
                        spellCardList = null;
                    }

                    chara = new Chara { Name = e.Value.Replace("&dagger;", "").TrimEnd(), Id = ++_charaId };
                    continue;
                }
                else
                {
                    var spells = e.Descendants(ns + "tr").Skip(1);
                    if (spellCardList == null)
                    {
                        spellCardList = new List<SpellCard>();
                    }

                    spellCardList.AddRange(spells.Select(x => x.Elements(ns + "td"))
                        .Select(x => new SpellCard
                        {
                            Name = x.ElementAt(0).Value,
                            Seriese = x.ElementAt(1).Value.Split('、'),
                            Id = ++_spellId
                        })
                    );

                }
            }

            chara.SpellCards = spellCardList;
            charaList.Add(chara);

            return charaList;
        }
    }

    class Chara
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public List<SpellCard> SpellCards { get; set; }

        public XElement ToXml()
        {
            var charaNode = new XElement("Character");
            charaNode.SetAttributeValue("id", Id);
            charaNode.SetAttributeValue("name", Name);
            var spellCardsNode = new XElement("SpellCards");

            spellCardsNode.Add(SpellCards.Select(x =>
            {
                var n = new XElement("SpellCard");
                n.SetAttributeValue("id", x.Id);
                n.SetAttributeValue("name", x.Name);
                n.Add(x.Seriese.Select(y => new XElement("Seriese", y)));

                return n;
            }));

            charaNode.Add(spellCardsNode);
            return charaNode;
        }
    }

    class SpellCard
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string[] Seriese { get; set; }
    }
}