// Evently — mobile navigation
document.addEventListener('DOMContentLoaded', function () {
    var btn = document.getElementById('mobile-menu-btn');
    var nav = document.getElementById('mobile-nav');
    if (btn && nav) {
        btn.addEventListener('click', function () {
            nav.classList.toggle('hidden');
            nav.classList.toggle('flex');
        });
    }

    // Category filter buttons on events page (visual toggle)
    document.querySelectorAll('[data-category-filter]').forEach(function (button) {
        button.addEventListener('click', function () {
            document.querySelectorAll('[data-category-filter]').forEach(function (b) {
                b.classList.remove('bg-gradient-primary', 'text-primary-foreground', 'border-transparent', 'shadow-glow');
                b.classList.add('glass', 'border-white/10', 'hover:bg-white/10');
            });
            this.classList.remove('glass', 'border-white/10', 'hover:bg-white/10');
            this.classList.add('bg-gradient-primary', 'text-primary-foreground', 'border-transparent', 'shadow-glow');
        });
    });
});
