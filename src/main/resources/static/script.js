 function showMenu(category, btn) {
        // Filter cards
        const cards = document.querySelectorAll('.card-container .card');
        cards.forEach(card => {
            if(category === 'All' || card.dataset.category === category){
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });

        // Handle active button
        const buttons = document.querySelectorAll('.menu-btn');
        buttons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
    }

