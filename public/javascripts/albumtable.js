(function($, global) {
    $(document).ready(function() {
        
        var voteInfo = $('#voteInfo'),
        
        voteLink = $('a.voteLink'),
        
        list = $('#albumList'),
        
        covers = $('.cover'),
        
        nbVotes = $('span[id^="nbVotes"]'),

        //Click on vote link
        clickVote = function(e) {
            // in this context, e.target is the same as this
            var t = $(e.target),
            id = t.attr('id').split('-')[0],
            voteTarget = nbVotes.filter("[id$=" + id + "]");
            
            // only one vote is possible for an album
            t.hide();
                        
            $.ajax({
                url: '/application/vote',
                type: "POST",
                data: {id: id},
                complete: function(req) {
                    // beware of js particular scope      
                    var newTotal = req.responseText;

                    if (req.status === 200) {
                        voteTarget.text(newTotal);
                        voteInfo.slideDown("slow").delay(3000).slideUp("slow");
                    }
                }
            });
        },

        //Display cover in datatable
        displayCover = function(id, albumMarkup){
            var root = '/public/shared/covers';
            var markup = '<img src="' + root + '/' + id + '" width="200" height="197">';
            albumMarkup.bt(markup, {
                width: 200,
                fill: 'white',
                cornerRadius: 20,
                padding: 20,
                strokeWidth: 1,
                trigger: ['mouseover', 'click']
            });
        };

        //format data table
        list.dataTable();

        covers.each(function(i, val) {
            var t = $(this);
            
            var album = t.closest('tr').attr("id");
            var id = album.match(/album-(\d+)/)[1];

            displayCover(id, t);
        });
        voteLink.click(clickVote);          
    });
})(this.jQuery, this);