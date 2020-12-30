// Brunch automatically concatenates all files in your
// watched paths. Those paths can be configured at
// config.paths.watched in "brunch-config.js".
//
// However, those files will only be executed if
// explicitly imported. The only exception are files
// in vendor, which are never wrapped in imports and
// therefore are always executed.

// Import dependencies
//
// If you no longer want to use a dependency, remember
// to also remove its path from "config.paths.watched".
import 'phoenix_html';

// Import local files
//
// Local files can be imported directly using relative
// paths "./socket" or full ones "web/static/js/socket".

// import socket from "./socket"

// https://medium.com/@diamondgfx/writing-a-full-site-in-phoenix-and-elm-a100804c9499
import Elm from './main';
const elmDiv = document.querySelector('#elm-target');
if (elmDiv) {
  var localStoragePorts = require("elm-local-storage-ports");
  var elmApp = Elm.Main.embed(elmDiv);
  localStoragePorts.register(elmApp.ports);
}