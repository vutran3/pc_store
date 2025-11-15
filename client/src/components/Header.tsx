import React from 'react';
import { Button } from '@/components/ui/button';
import { Link, useNavigate } from 'react-router-dom';
import { Menu, Monitor, Search, ShoppingCart } from 'lucide-react';
import { useSelector } from 'react-redux';
import { RootState } from '@/redux/store';

export default function Header() {
  const navigate = useNavigate();
  const totalQuantity = useSelector((state: RootState) => state.cart?.totalQuantity ?? 0);

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
          <div className="relative flex-1 max-w-md hidden md:block">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <input type="search" placeholder="Search products..." className="w-full h-9 rounded-md border bg-background px-8 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring" />
          </div>

          <div className="flex items-center gap-3">
            <button onClick={() => navigate('/cart')} className="relative p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <ShoppingCart className="w-6 h-6 text-gray-700" />
              {totalQuantity > 0 && (
                <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs w-5 h-5 rounded-full flex items-center justify-center font-bold">
                  {totalQuantity}
                </span>
              )}
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}
