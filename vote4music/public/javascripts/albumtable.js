/* 
 * Script for albumtable.html template
 */
$(document).ready(function() {
        $("#voteInfo").hide();
        $('#albumList').dataTable();
        //album covers
        $.each($('.cover'), function(){
            var album = $(this).parent().parent().attr("id");
            //album id
            var id = album.substring(6);
            displayCover(id, $(this));
        });
        //vote link
        $('a.voteLink').click(function(){
           var albumId = parseInt(this.id);
           clickVote(albumId);
           //only one vote is possible for an album
           $('#'+albumId+'-clickVote').hide();
      });
    });
    
var clickVote = function(id) {
    $.ajax({
        url: '/vote',
        type: "POST",
        data: 'id=' + id,
        complete: function(req) {
            //success
            if (req.status == 200) {
                var newTotal = req.responseText;
                $('#nbVotes'+id).text(newTotal);
                $("#voteInfo").slideDown("slow").delay(3000).slideUp("slow");
            }
        }
    });
};

var displayCover = function(id, albumMarkup){
        var root = '/public/shared/covers';
        var markup = '<img src="' + root + '/' + id + '" width="200" height="197">';
        albumMarkup.bt(markup,
        {
            width: 200,
            fill: 'white',
            cornerRadius: 20,
            padding: 20,
            strokeWidth: 1,
            trigger: ['mouseover', 'click']
        });
    }
