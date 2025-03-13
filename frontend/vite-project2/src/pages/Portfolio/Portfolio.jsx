import CompanyNavbar from "../../components/CompanyNavbar";
import { Search } from "lucide-react"; // Make sure to import Search icon
const Portfolio = () => {
  const menuItems = [
    { title: "Projects", icon: "📦" },
    { title: "Portfolio", icon: "💼" },
    { title: "Create Project", icon: "🏠➕" },
    { title: "Customer Reviews", icon: "⭐" },
    { title: "Tenders", icon: "💰🤝" },
  ];

  return (
    <div className="flex h-screen">
      <CompanyNavbar />
      <main className="flex-grow bg-gray-100 p-6">
        <header className="flex justify-between items-center mb-6">
          <h1 className="text-xl font-semibold">DP Constructions</h1>
          <div className="relative">
            <input
              type="text"
              placeholder="Search"
              className="pl-10 pr-4 py-2 rounded-full border border-gray-300 shadow-sm focus:outline-none"
            />
            <Search className="absolute left-3 top-2 text-gray-500" size={18} />
          </div>
        </header>

        <div className="grid grid-cols-3 gap-6 max-w-lg">
          {menuItems.map((item, index) => (
            <div
              key={index}
              className="flex flex-col items-center justify-center p-6 bg-yellow-500 rounded-xl shadow-md cursor-pointer hover:bg-yellow-600 transition-colors"
            >
              <span className="text-3xl mb-2">{item.icon}</span>
              <span className="font-semibold">{item.title}</span>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default Portfolio;
