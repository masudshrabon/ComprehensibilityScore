using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

// Namespace declaration
namespace KeywordDemo
{
    // Interface
    interface IDemo
    {
        void Run();
    }

    // Base class
    abstract class Base : IDemo
    {
        public abstract void Run();
    }

    // Sealed class with unsafe and fixed usage
    sealed class Program : Base
    {
        // Enum
        enum Colors { Red, Green, Blue }

        // Struct
        struct Point
        {
            public int x, y;
        }

        // Delegate
        delegate void MyDelegate(string message);

        // Event
        public event MyDelegate OnMessage;

        // Constant
        const double PI = 3.14159;

        // Readonly
        readonly int id = 1;

        // Field
        volatile bool isRunning;

        // Property
        public string Name { get; set; } = "Demo";

        // Constructor
        public Program() => this.OnMessage += msg => Console.WriteLine(msg);

        // Override
        public override void Run()
        {
            try
            {
                checked
                {
                    int x = 100;
                    x = x * 10000;
                }

                int? nullableInt = null;
                object obj = null;
                dynamic dyn = "Hello";
                Console.WriteLine($"{dyn}");

                List<int> list = new() { 1, 2, 3 };
                foreach (var item in list)
                {
                    if (item is int val && val > 0)
                    {
                        Console.WriteLine($"Item: {val}");
                    }
                }

                var result = from n in list
                             where n > 1
                             select n;

                foreach (var r in result)
                {
                    Console.WriteLine(r);
                }

                unsafe
                {
                    int i = 5;
                    int* p = &i;
                    Console.WriteLine(*p);
                }

                fixed (char* p = "hello")
                {
                    Console.WriteLine(*p);
                }

                OnMessage?.Invoke("Everything ran!");
            }
            catch (Exception ex) when (ex is ArgumentNullException)
            {
                Console.WriteLine("Caught null arg exception.");
            }
            finally
            {
                Console.WriteLine("Finally block.");
            }
        }

        // Operator overload
        public static Program operator +(Program a, Program b) => new Program();

        // Indexer
        public int this[int index]
        {
            get => index * 2;
            set { }
        }

        // Finalizer
        ~Program() => Console.WriteLine("Destructor called");

        // Main entry
        static async Task Main(string[] args)
        {
            await Task.Run(() =>
            {
                Program p = new();
                p.Run();

                lock (p)
                {
                    for (int i = 0; i < 3; i++)
                    {
                        while (true)
                        {
                            break;
                        }
                    }
                }

                switch (p.id)
                {
                    case 1:
                        goto case 2;
                    case 2:
                        Console.WriteLine("Case 2");
                        break;
                    default:
                        break;
                }

                bool condition = true;
                do
                {
                    condition = false;
                } while (condition);

                int num = 5;
                Console.WriteLine(num switch
                {
                    > 0 => "Positive",
                    < 0 => "Negative",
                    _ => "Zero"
                });

                var tuple = (x: 1, y: 2);
                ref readonly var refTuple = ref tuple;

                Span<int> span = stackalloc int[3];
            });
        }
    }
}
