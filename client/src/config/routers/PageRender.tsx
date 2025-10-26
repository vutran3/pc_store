import { NotFound } from "@/pages";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
// import { useSelector } from "react-redux";
// import { RootState } from "../../redux/store";

const pages: any = import.meta.glob("../../components/pages/**/*.tsx");

const PageRender: React.FC = () => {
  const { page, id } = useParams<{ page: string; id: string }>();
  const [Component, setComponent] = useState<React.ComponentType | null>(null);
  const [notFound, setNotFound] = useState<boolean>(false);
  // const { isLogin } = useSelector((state: RootState) => state.user);

  useEffect(() => {
    const loadComponent = async () => {
      if (page) {
        const formatPage = page.charAt(0).toUpperCase() + page.slice(1);
        const pagePath = id
          ? `../../components/pages/${formatPage}/[id].tsx`
          : `../../components/pages/${formatPage}.tsx`;

        if (pages[pagePath]) {
          try {
            const module = await pages[pagePath]();
            setComponent(() => module.default);
            setNotFound(false);
          } catch (error) {
            setNotFound(true);
            setComponent(null);
          }
        } else {
          setNotFound(true);
          setComponent(null);
        }
      } else {
        setNotFound(true);
        setComponent(null);
      }
    };

    loadComponent();
  }, [page, id]);

  if (notFound) return <NotFound />;
  if (Component) return <Component />;

  return null;
};

export default PageRender;
