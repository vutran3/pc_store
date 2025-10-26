import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/hooks/use-toast";
import { RootState } from "@/redux/store";
import { register } from "@/redux/thunks/auth";
import { Auth } from "@/types";
import { RegisterCredentialsSchema } from "@/types/Auth";
import { Loader2, Monitor } from "lucide-react";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { z } from "zod";

export default function Register() {
  const { toast } = useToast();
  const [registerCredentials, setRegisterCredentials] =
    useState<Auth.RegisterCredentials>({
      userName: "",
      password: "",
      firstName: "",
      lastName: "",
      email: "",
      phoneNumber: "",
    });
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { status } = useSelector((state: RootState) => state.auth);

  const handleRegister = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      RegisterCredentialsSchema.parse(registerCredentials);
      const result = await dispatch(
        register(registerCredentials) as Auth.RegisterResponse | any
      );
      if (register.fulfilled.match(result)) {
        toast({
          title: "Đăng ký thành công",
          description: "Chào mừng bạn đã đăng ký tài khoản!",
        });
        navigate("/login");
      } else {
        toast({
          title: "Đăng ký thất bại",
          description: "Thông tin đăng ký không hợp lệ",
          variant: "destructive",
        });
      }
    } catch (err) {
      if (err instanceof z.ZodError) {
        const errorMessage = err.errors[0].message;
        toast({
          variant: "destructive",
          description: errorMessage,
        });
      } else {
        toast({
          variant: "destructive",
          description: "Đã xảy ra lỗi không xác định",
        });
      }
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <div className="flex flex-col items-center gap-6">
          <div className="flex items-center gap-2">
            <Monitor className="h-6 w-6 text-blue-600" />
            <h1 className="text-2xl font-bold text-gray-900">PC Store</h1>
          </div>

          <div className="text-center space-y-2">
            <h2 className="text-3xl font-semibold tracking-tight">Đăng ký</h2>
            <p className="text-sm text-muted-foreground">
              Nhập thông tin đăng ký của bạn để tạo tài khoản
            </p>
          </div>

          <form onSubmit={handleRegister} className="w-full space-y-4">
            <div className="space-y-2">
              <Label htmlFor="username">Tên đăng nhập</Label>
              <Input
                id="username"
                type="text"
                placeholder="Nhập tên đăng nhập"
                value={registerCredentials.userName}
                onChange={(e) =>
                  setRegisterCredentials({
                    ...registerCredentials,
                    userName: e.target.value,
                  })
                }
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Mật khẩu</Label>
              <Input
                id="password"
                type="password"
                placeholder="Nhập mật khẩu"
                value={registerCredentials.password}
                onChange={(e) =>
                  setRegisterCredentials({
                    ...registerCredentials,
                    password: e.target.value,
                  })
                }
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">Họ</Label>
                <Input
                  id="firstName"
                  type="text"
                  placeholder="Nhập họ"
                  value={registerCredentials.firstName}
                  onChange={(e) =>
                    setRegisterCredentials({
                      ...registerCredentials,
                      firstName: e.target.value,
                    })
                  }
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="lastName">Tên</Label>
                <Input
                  id="lastName"
                  type="text"
                  placeholder="Nhập tên"
                  value={registerCredentials.lastName}
                  onChange={(e) =>
                    setRegisterCredentials({
                      ...registerCredentials,
                      lastName: e.target.value,
                    })
                  }
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="Nhập email"
                value={registerCredentials.email}
                onChange={(e) =>
                  setRegisterCredentials({
                    ...registerCredentials,
                    email: e.target.value,
                  })
                }
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="phoneNumber">Số điện thoại</Label>
              <Input
                id="phoneNumber"
                type="tel"
                placeholder="Nhập số điện thoại"
                value={registerCredentials.phoneNumber}
                onChange={(e) =>
                  setRegisterCredentials({
                    ...registerCredentials,
                    phoneNumber: e.target.value,
                  })
                }
              />
            </div>

            <Button
              disabled={status === "loading"}
              type="submit"
              className="w-full"
            >
              {status === "loading" ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Đang đăng ký...
                </>
              ) : (
                "Đăng ký"
              )}
            </Button>

            <p className="text-center text-sm text-gray-600">
              Đã có tài khoản?{" "}
              <Link
                to="/login"
                className="font-medium text-blue-600 hover:underline"
              >
                Đăng nhập ngay
              </Link>
            </p>
          </form>
        </div>
      </div>
    </div>
  );
}
