/* 
 * Script for albumtable.html template
 */
$(document).ready(function() {
        $("#voteInfo").hide();
        $('#albumList').dataTable();
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
