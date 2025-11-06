import { Button } from "@/components/ui/button";
import { Link, useNavigate } from "react-router-dom";
import {
    Cpu,
    HardDrive,
    Keyboard,
    Mouse
} from "lucide-react";

const Home = () => {
    const navigate = useNavigate();
    return (
        <div className="min-h-screen bg-background flex flex-col items-center">
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
                    <Button size="lg" onClick={() => navigate("/products")}>
                        Shop Now
                    </Button>
                </div>
            </section>

            {/* Categories */}
            <section className="container py-12">
                <h2 className="text-2xl font-bold tracking-tight mb-8">
                    Shop by Category
                </h2>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                    <Link
                        className="flex flex-col items-center justify-center p-6 rounded-lg border hover:border-foreground transition-colors"
                        to="/category/processors"
                    >
                        <Cpu className="h-12 w-12 mb-4" />
                        <h3 className="font-semibold">Processors</h3>
                    </Link>
                    <Link
                        className="flex flex-col items-center justify-center p-6 rounded-lg border hover:border-foreground transition-colors"
                        to="/category/storage"
                    >
                        <HardDrive className="h-12 w-12 mb-4" />
                        <h3 className="font-semibold">Storage</h3>
                    </Link>
                    <Link
                        className="flex flex-col items-center justify-center p-6 rounded-lg border hover:border-foreground transition-colors"
                        to="/category/keyboards"
                    >
                        <Keyboard className="h-12 w-12 mb-4" />
                        <h3 className="font-semibold">Keyboards</h3>
                    </Link>
                    <Link
                        className="flex flex-col items-center justify-center p-6 rounded-lg border hover:border-foreground transition-colors"
                        to="/category/mice"
                    >
                        <Mouse className="h-12 w-12 mb-4" />
                        <h3 className="font-semibold">Mice</h3>
                    </Link>
                </div>
            </section>
        </div>
    );
}

export default Home;
