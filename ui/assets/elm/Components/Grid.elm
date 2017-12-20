module Components.Grid exposing (view)

import Html exposing (Html, text, table, thead, tfoot, tbody, tr, td)
import Html.Attributes exposing (class)


{-
<table class="narwhal-grid-table">
  <thead></thead>
  <tfoot></tfoot>
  <tbody>
    <%= for x <- 1..16 do %>
      <tr class="narwhal-grid-row">
        <%= for y <- 1..16 do %>
          <td class="narwhal-grid-cell"
              style="background-color:<%= get_color(x, y)%>">
             &nbsp;
          </td>
        <% end %>
      </tr>
    <% end %>
  </tbody>
</table>
-}

gridData : List (List number)
gridData = [ [1, 2, 3], [11, 22, 33], [ 111, 222, 333 ] ]

renderCell : number -> Html a
renderCell n = 
  td [ class "narwhal-grid-row" ] [ text (toString n) ]

renderRow : List number -> Html a
renderRow row = 
  tr [] List.map renderCell row

renderRows : List (Html a)
renderRows = 
  List.map renderRow gridData

view : Html a
view =
  table [ class "narwhal-grid-table" ] [
    thead [] [],
    tfoot [] [],
    tbody [] renderRows
  ]
