@import hexlet.code.model.Url
@import java.time.format.DateTimeFormatter

@param hexlet.code.dto.UrlIndexPage page

@template.layouts.page(
page = page,
content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайты</h1>
            <table class="table table-bordered table-hover mt-3">
                <thead>
                    <tr>
                        <th class="col-1">ID</th>
                        <th>Имя</th>
                        <th class="col-2">Последняя проверка</th>
                        <th class="col-1">Код ответа</th>
                    </tr>
                </thead>
                <tbody>
                    @for (Url url : page.getUrls())
                        <tr>
                            <td>${url.getId()}</td>
                            <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                            <td>@if (url.getLastCheckAt() != null) ${url.getLastCheckAt().format(DateTimeFormatter.ofPattern("dd/MM/YYYY kk:mm:ss"))} @endif</td>
                            <td>@if (url.getLastCheckAt() != null) ${url.getLastStatusCode()} @endif</td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        </div>
    </section>
`,
)
