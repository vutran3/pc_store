import { Button } from "@/components/ui/button";
import {
  Cpu,
  HardDrive,
  Keyboard,
  Menu,
  Monitor,
  Mouse,
  Search,
  ShoppingCart,
} from "lucide-react";

function App() {
  return (
    <div className="min-h-screen bg-background flex flex-col items-center">
      {/* Header */}
      <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 flex justify-center items-center">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-4">
            <Button variant="outline" size="icon" className="md:hidden">
              <Menu className="h-5 w-5" />
            </Button>
            <div className="hidden md:flex items-center gap-6">
              <a className="flex items-center gap-2" href="/">
                <Monitor className="h-6 w-6" />
                <span className="font-bold">PC Store</span>
              </a>
              <nav className="flex items-center gap-6 text-sm font-medium">
                <a
                  className="hover:text-foreground/80 transition-colors"
                  href="/products"
                >
                  Products
                </a>
                <a
                  className="hover:text-foreground/80 transition-colors"
                  href="/builds"
                >
                  PC Builds
                </a>
                <a
                  className="hover:text-foreground/80 transition-colors"
                  href="/about"
                >
                  About
                </a>
              </nav>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <div className="relative flex-1 max-w-md">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <input
                type="search"
                placeholder="Search products..."
                className="w-full h-9 rounded-md border bg-background px-8 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
              />
            </div>
            <Button variant="outline" size="icon">
              <ShoppingCart className="h-5 w-5" />
            </Button>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="container py-12">
        <div className="rounded-lg bg-card px-6 py-10 md:px-12 md:py-16 text-center">
          <h1 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl mb-4">
            Build Your Dream PC
          </h1>
          <p className="text-muted-foreground max-w-[700px] mx-auto mb-8">
            High-performance custom PCs built with premium components. Find the
            perfect parts for your next build.
          </p>
          <Button size="lg">Shop Now</Button>
        </div>
      </section>

      {/* Categories */}
      <section className="container py-12">
        <h2 className="text-2xl font-bold tracking-tight mb-8">
          Shop by Category
        </h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          <a
            className="flex flex-col items-center justify-center p-6 rounded-lg border hover:border-foreground transition-colors"
            href="/category/processors"
          >
            <Cpu className="h-12 w-12 mb-4" />
            <h3 className="font-semibold">Processors</h3>
          </a>
          <a
            className="flex flex-col items-center justify-center p-6 rounded-lg border hover:border-foreground transition-colors"
            href="/category/storage"
          >
            <HardDrive className="h-12 w-12 mb-4" />
            <h3 className="font-semibold">Storage</h3>
          </a>
          <a
            className="flex flex-col items-center justify-center p-6 rounded-lg border hover:border-foreground transition-colors"
            href="/category/keyboards"
          >
            <Keyboard className="h-12 w-12 mb-4" />
            <h3 className="font-semibold">Keyboards</h3>
          </a>
          <a
            className="flex flex-col items-center justify-center p-6 rounded-lg border hover:border-foreground transition-colors"
            href="/category/mice"
          >
            <Mouse className="h-12 w-12 mb-4" />
            <h3 className="font-semibold">Mice</h3>
          </a>
        </div>
      </section>
    </div>
  );
}

export default App;
