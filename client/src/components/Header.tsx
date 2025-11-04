import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";
import {
    Menu,
    Monitor,
    Search,
    ShoppingCart,
} from "lucide-react";

const Header = () => {
    return (
        <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 flex justify-center items-center">
            <div className="container flex h-16 items-center justify-between">
                <div className="flex items-center gap-4">
                    <Button variant="outline" size="icon" className="md:hidden">
                        <Menu className="h-5 w-5" />
                    </Button>
                    <div className="hidden md:flex items-center gap-6">
                        <Link className="flex items-center gap-2" to="/">
                            <Monitor className="h-6 w-6" />
                            <span className="font-bold">PC Store</span>
                        </Link>
                        <nav className="flex items-center gap-6 text-sm font-medium">
                            <Link className="hover:text-foreground/80 transition-colors" to="/products">Products</Link>
                            <Link className="hover:text-foreground/80 transition-colors" to="/builds">PC Builds</Link>
                            <Link className="hover:text-foreground/80 transition-colors" to="/about">About</Link>
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
    );
}

export default Header;
