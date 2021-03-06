module Components.AppHtmlUtils exposing (IsValid, fieldErrorMessage, httpErrorText, inputStyle, nextLine, onChange, space)

import Html exposing (Attribute, Html, br, p, text)
import Html.Attributes exposing (class)
import Html.Events
import Http exposing (Error(..))
import Json.Decode
import Types.Message as Message exposing (Message)



-- Events


onChange : (String -> msg) -> Attribute msg
onChange handler =
    Html.Events.on "change" (Json.Decode.map handler Html.Events.targetValue)



-- Adjustment


space : String
space =
    "\u{00A0}"


nextLine : Html msg
nextLine =
    br [] []



-- Form


type alias IsValid =
    Bool


inputStyle : IsValid -> Attribute msg
inputStyle isValid =
    if isValid then
        class "input"

    else
        class "input is-danger"


fieldErrorMessage : Message -> Html msg
fieldErrorMessage err =
    if Message.isError err then
        p [ class "help is-danger" ] [ text (err |> Message.toString) ]

    else
        text ""



-- Error


httpErrorText : Http.Error -> Html msg
httpErrorText error =
    case error of
        BadUrl url ->
            text ("BadUrl:" ++ url)

        Timeout ->
            text "Timeout"

        NetworkError ->
            text "NetworkError"

        BadStatus status ->
            text ("BadStatus:" ++ String.fromInt status)

        BadBody body ->
            text ("BadBody:" ++ body)
