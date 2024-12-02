package service;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.CreateGameResult;
import model.GameListResult;
import dataaccess.DataAccessException;

public class GameService {
    private final DataAccess dataAccessor;

    public GameService(DataAccess dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    public GameListResult list(String authToken) throws ServerException {
        try {
            validateAuthToken(authToken);
            return new GameListResult(dataAccessor.getGameDAO().listGames());
        } catch (DataAccessException ex) {
            throw new ServerException(ex);
        }
    }

    public CreateGameResult create(String gameName, String authToken) throws ServerException {
        try {
            validateAuthToken(authToken);
            validateGameName(gameName);

            GameData newGame = new GameData(0, null, null, gameName, new ChessGame());
            GameData createdGame = dataAccessor.getGameDAO().addGame(newGame);

            return new CreateGameResult(createdGame.gameID());
        } catch (DataAccessException ex) {
            throw new ServerException(ex);
        }
    }

    public Object join(TeamColor color, int gameID, String authToken) throws ServerException {
        try {
            String username = validateAuthToken(authToken).username();
            GameData game = getGameById(gameID);

            validateColorSelection(color);
            validateColorAvailability(color, game);

            updateGameWithPlayer(color, username, game);

            return null;
        } catch (DataAccessException ex) {
            throw new ServerException(ex);
        }
    }

    private AuthData validateAuthToken(String authToken) throws ServerException {
        try {
            AuthData authData = dataAccessor.getAuthDAO().getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Error: Invalid auth token");
            }
            return authData;
        } catch (DataAccessException ex) {
            throw new ServerException(ex);
        }
    }

    private void validateGameName(String gameName) throws BadRequestException {
        if (gameName == null) {
            throw new BadRequestException("Error: Game must have a name");
        }
    }

    private GameData getGameById(int gameID) throws BadRequestException, DataAccessException {
        GameData game = dataAccessor.getGameDAO().getGame(gameID);
        if (game == null) {
            throw new BadRequestException("Error: Game you are joining is not available");
        }
        return game;
    }

    private void validateColorSelection(TeamColor color) throws BadRequestException {
        if (color == null) {
            throw new BadRequestException("Error: You must select a color");
        }
        if (color != TeamColor.WHITE && color != TeamColor.BLACK) {
            throw new BadRequestException("Error: Invalid color. Must be WHITE or BLACK");
        }
    }

    private void validateColorAvailability(TeamColor color, GameData game) throws AlreadyTakenException {
        if ((color == TeamColor.BLACK && game.blackUsername() != null)
                || (color == TeamColor.WHITE && game.whiteUsername() != null)) {
            throw new AlreadyTakenException("Error: Color is already taken %s".formatted(game.toString()));
        }
    }

    private void updateGameWithPlayer(TeamColor color, String username, GameData game) throws DataAccessException {
        if (color == TeamColor.BLACK) {
            dataAccessor.getGameDAO().updateGame(game.setBlackUsername(username));
        } else {
            dataAccessor.getGameDAO().updateGame(game.setWhiteUsername(username));
        }
    }
}