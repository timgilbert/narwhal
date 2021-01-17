defmodule NarwhalUiWeb.PageController do
  use NarwhalUiWeb, :controller

  def index(conn, _params) do
    render(conn, "index.html")
  end
end
