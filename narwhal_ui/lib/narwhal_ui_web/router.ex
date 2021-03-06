defmodule NarwhalUiWeb.Router do
  use NarwhalUiWeb, :router

  pipeline :browser do
    plug :accepts, ["html"]
    plug :fetch_session
    plug :fetch_flash
    plug :protect_from_forgery
    plug :put_secure_browser_headers
  end

  pipeline :api do
    plug :accepts, ["json"]
  end

  scope "/", NarwhalUiWeb do
    pipe_through :browser

    # TODO: should return this on anything not otherwise matched
    get "/", PageController, :index
    get "/frame", PageController, :index
    get "/frame/*any", PageController, :index
    get "/timeline", PageController, :index
    get "/timeline/*any", PageController, :index
  end

  forward "/graphql", Absinthe.Plug,
          schema: NarwhalUiWeb.Schema

  forward "/graphiql",
          Absinthe.Plug.GraphiQL,
          schema: NarwhalUiWeb.Schema

  # Other scopes may use custom stacks.
   scope "/api", NarwhalUiWeb do
     pipe_through :api
   end

  # Enables LiveDashboard only for development
  #
  # If you want to use the LiveDashboard in production, you should put
  # it behind authentication and allow only admins to access it.
  # If your application does not have an admins-only section yet,
  # you can use Plug.BasicAuth to set up some basic authentication
  # as long as you are also using SSL (which you should anyway).
  if Mix.env() in [:dev, :test] do
    import Phoenix.LiveDashboard.Router

    scope "/" do
      pipe_through :browser
      live_dashboard "/dashboard", metrics: NarwhalUiWeb.Telemetry
    end
  end
end
