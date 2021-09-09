using System.Text.Json;
using Newtonsoft.Json;

namespace DotNetFunction
{
    public class Book
    {
        public string id{ get; set; }

        public string name { get; set; }

        public string author{ get; set; }
    }
}