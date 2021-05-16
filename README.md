Chameleody: Music Player with most features which other music players have and more original features. I haven't finished it yet. But, It is enough ready to use.

Original Features that I am going to add and which is the reason I am developing and loving this project:
Ability to add every information about the song: mood, genre, version, age added; also will be added bpm, singer's voice, 
Ability to add smart templates, which later you can use for smart search and smart shuffle.
It is like "Version : Original - 100%, Remix - 60%, Cover - 20%, Instrumental - 0%". And for all other classifications
When you will use it to searching it will show all bigger than 0% or only 100%. If You use it for smart shuffle, I think you understand how it will work

Why I am doing this:
Everyone uses shuffle in music players, But I wanted a smarter one, I used a 5-star classification of AIMP, but it wasn't enough. Your specifications are changeable of your situation, mood. Sometimes you are sad, sometimes you energized, sometimes you are just only what to hear NightCore remixes, sometimes you want calm, dreaming songs.


Final Project requirements:  
Link to a repository (30 pts max)✅  
Use of data storage (DB, Cloud or SharedPreferences) (20 pts max)✅ SharedPreferences, Room. (Also Firebase will be added, now there is only json file)  
Design and representation (figma design first is welcome) (20 pts max)✅ [link](https://www.figma.com/file/5z4MmlFdfMtPN0hO2KZBKG/Chameleody?node-id=0%3A1)  
Wise use of libraries and frameworks (20 pts max)✅ [gradle](https://github.com/DOSLAN/Chameleody/blob/main/app/build.gradle) Glide; Room; Coroutines; Lifecycle, Palette and Media from androidx  
Clean code (10 pts max)❌  
BONUS POINTS FOR (20 pts):  
Projects related to our University❌  
Projects which use Dagger, Glide other tools✅ Used Glide for all cover art displays and getting a palette of cover art in the player window
Submit link to repository:  
commit history elder than 1 week (10 pts)✅  
apk in releases (5 pts)✅ will be  
screenshots, name and short description (15 pts)✅ will be  

## Screenshots of UI
<table>
  <tr>
    <td>Main window</td>
    <td>Player window</td>
  </tr>
  <tr>
    <td><img src="screenshots/main.jpg" width=400></td>
    <td><img src="screenshots/player.jpg" width=400></td>
  </tr>
 </table>

<table><tr><td>UI colors of Player will change according to song's art's color</td></tr></table>
<table>
 <tr>
  <td><img src="screenshots/player.jpg"></td>
  <td><img src="screenshots/player_2.jpg"></td>
  <td><img src="screenshots/repeat_all.jpg"></td>
 </tr>
</table>

<table><tr><td>Player has four tabs</td></tr></table>
<table>
  <tr>
    <td>Lyrics</td>
    <td>Cover art</td>
    <td>General info</td>
    <td>Mood classification</td>
  </tr>
 <tr>
  <td><img src="screenshots/lyrics.jpg"></td>
  <td><img src="screenshots/player_2.jpg"></td>
  <td><img src="screenshots/info.jpg"></td>
  <td><img src="screenshots/mood.jpg"></td>
 </tr>
</table>
 
 ## Screenshots of misc capabilities
 
<table>
 <tr>
  <td> App icon </td>
  <td> Logo </td>
  <td> Asking for permission </td>
 </tr>
 <tr>
  <td><img src="screenshots/icon.jpg" width=400></td>
  <td><img src="screenshots/logo.jpg" width=400></td>
  <td><img src="screenshots/permission.jpg" width=400></td>
 </tr>
</table>

<table>
 <tr>
  <td> Searching songs</td>
  <td> Sorting songs list</td>
  <td> Notification </td>
 </tr>
 <tr>
  <td><img src="screenshots/search.jpg" width=400></td>
  <td><img src="screenshots/sort.jpg" width=400></td>
  <td><img src="screenshots/notification.jpg" width=400></td>
 </tr>
</table>

<table><tr><td>Player has four shuffle types, on the left of play button, on the right new feature which has not implemented</td></tr></table>
<table>
  <tr>
    <td>Repeat one, when next or prev pressed it will work like a Repeat All</td>
    <td>Repeat All</td>
    <td>Shuffle All</td>
    <td>Smart shuffle, this one doesn't implemented yet. It is going to be one of the original features</td>
  </tr>
 <tr>
  <td><img src="screenshots/repeat_one.jpg"></td>
  <td><img src="screenshots/repeat_all.jpg"></td>
  <td><img src="screenshots/shuffle_all.jpg"></td>
  <td><img src="screenshots/shuffle_smart.jpg"></td>
 </tr>
</table>
