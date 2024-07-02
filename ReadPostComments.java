import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

public class ReadPostComments {
    public static void main(String[] args) {
        readCommentsFromEveryPostOfSpecificUser(1);
    }

    public static void readCommentsFromEveryPostOfSpecificUser(int userId) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type postListType = new TypeToken<List<Post>>(){}.getType();
        List<Post>listOfUserPosts = new ArrayList<>();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/" + userId + "/posts"))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> userPosts = client.send(request, HttpResponse.BodyHandlers.ofString());
            listOfUserPosts =  gson.fromJson(userPosts.body(),postListType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Integer> listOfPostIds = listOfUserPosts.stream()
                .map(post -> post.getId())
                .collect(Collectors.toList());


        listOfPostIds.stream()
                .forEach((number)->{
                    HttpRequest request1 =  HttpRequest.newBuilder()
                            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/"+number+"/comments"))
                            .GET()
                            .build();
                    HttpClient client1 = HttpClient.newHttpClient();
                    try {
                        HttpResponse<String> response = client1.send(request1, HttpResponse.BodyHandlers.ofString());
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user-"+userId+"-post-"+number+"-comments.json"))){
                            writer.write(response.body());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });


    }
}

class Post {
    private int userId;
    private int id;
    private String title;
    private String body;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

